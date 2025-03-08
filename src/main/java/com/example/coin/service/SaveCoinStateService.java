package com.example.coin.service;


import com.example.coin.entity.CoinName;
import com.example.coin.repository.CoinInfoRepository;
import com.example.coin.repository.CoinNameRepository;
import com.example.coin.repository.SaveCoinStateRepository;
import com.example.coin.scheduler.CoinPriceScheduler;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


@Service
@RequiredArgsConstructor
public class SaveCoinStateService {
    private final SaveCoinStateRepository saveCoinStateRepository;
    private final CoinInfoRepository coinInfoRepository;
    private final CoinNameRepository coinNameRepository;
    public static final AtomicLong idNum = new AtomicLong(1); //얘는 공유객체 필요없네.

    ExecutorService executor;
    private CoinPriceScheduler oneM;
    private CoinPriceScheduler threeM;
    private CoinPriceScheduler fiveM;
    private CoinPriceScheduler daily;
    @PostConstruct //bean 주입이 다 끝나고 동작하게 만드는 어노테이션
    public void createScheduler(){ //스케줄러 객체 생성을 위한 함수.
        //스레드 풀을 공유객체로 만듦. 이제 executor을 조작해서 스레드를 독점하지 못하게 해야함.
        int numThreads = Runtime.getRuntime().availableProcessors()*2;
        executor = Executors.newFixedThreadPool(numThreads);
        oneM = new CoinPriceScheduler(saveCoinStateRepository,"1m",idNum,coinInfoRepository,coinNameRepository);
        threeM=new CoinPriceScheduler(saveCoinStateRepository,"3m",idNum,coinInfoRepository,coinNameRepository);// 스케줄러 객체
        fiveM=new CoinPriceScheduler(saveCoinStateRepository,"5m",idNum,coinInfoRepository,coinNameRepository);
        daily=new CoinPriceScheduler(saveCoinStateRepository,"24h",idNum,coinInfoRepository,coinNameRepository);
    }

    @Transactional
    public void saveCoinName() throws JSONException, IOException {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.bithumb.com/v1/market/all?isDetails=false";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        JSONArray json = new JSONArray(body.string());
        long startTime = System.currentTimeMillis();
        for(int i =0;i< json.length();i++){
            JSONObject jsonObject = new JSONObject(json.get(i).toString());

            String tempParse[] = jsonObject.getString("market").split("-");
            Optional<CoinName> existingCoinName = coinNameRepository.findByCoinName(tempParse[1]);

            String englishName  =  jsonObject.getString("english_name");
            String koreanName =  jsonObject.getString("korean_name");
            if (!existingCoinName.isPresent()) { //중복된 값이 아니면
                CoinName coinName = CoinName.builder().
                        coinName(tempParse[1]).
                        englishName(englishName).
                        koreanName(koreanName).
                        build();
                saveCoinStateRepository.saveCoinName(coinName);
            }
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("Total time for inserts: " + totalTime + " ms");

    }


    public void saveCoinPrice() throws InterruptedException {
        OkHttpClient client = new OkHttpClient();
        String[] state = {"1m", "3m", "5m", "10m", "15m", "30m", "1h", "4h", "6h", "12h", "24h", "1w", "1mm"};
        String url = "https://api.bithumb.com/public/candlestick/";

        List<String> coinNameList = saveCoinStateRepository.getAllCoinName();
        long startTime = System.currentTimeMillis();

        String sqlTemplate = "INSERT INTO coin_info (id, coin_date, opening_price, closing_price, max_price, min_price, units_traded, coin_name, state) VALUES ";

        int numThreads = Runtime.getRuntime().availableProcessors() * 2;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (String s : state) {
            executor.submit(() -> {
                // = getInitialIdNum(); // Method to get initial idNum
                StringBuilder localSql = new StringBuilder();
                localSql.append(sqlTemplate);

                for (String c : coinNameList) {
                    try {
                        Request request = new Request.Builder()
                                .url(url + c + "_KRW/" + s)
                                .get()
                                .addHeader("accept", "application/json")
                                .build();

                        Response response = client.newCall(request).execute();

                        ResponseBody body = response.body();
                        JSONObject json = new JSONObject(body.string());
                        JSONArray dataJson = new JSONArray(json.get("data").toString());

                        for (int i = 0; i < dataJson.length(); i++) {

                            JSONArray rowData = dataJson.getJSONArray(i);
                            long currentId = idNum.getAndIncrement(); // Atom
                            localSql.append("(");
                            localSql.append(currentId).append(", '");
                            localSql.append(rowData.get(0).toString()).append("', '");
                            localSql.append(rowData.get(1).toString()).append("', '");
                            localSql.append(rowData.get(2).toString()).append("', '");
                            localSql.append(rowData.get(3).toString()).append("', '");
                            localSql.append(rowData.get(4).toString()).append("', '");
                            localSql.append(rowData.get(5).toString()).append("', '");
                            localSql.append(c).append("', '");
                            localSql.append(s);
                            localSql.append("'),");
                        }

                        // After processing, insert into the database
                        if (localSql.length() > sqlTemplate.length()) {
                            // Remove the last comma
                            String finalSql = localSql.substring(0, localSql.length() - 1);
                            saveCoinStateRepository.saveCoinInfo(finalSql);

                            // Reset localSql for the next batch
                            localSql.setLength(0);
                            localSql.append(sqlTemplate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();
        if (executor.awaitTermination(10, TimeUnit.MINUTES)) {
            System.out.println("All tasks completed.");
        } else {
            System.out.println("Timeout occurred before termination.");
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time for inserts: " + totalTime + " ms");
    }

//    private synchronized int getInitialIdNum() {
//        // Synchronize this method if idNum needs to be unique across threads
//        // Alternatively, use an AtomicInteger for thread-safe increments
//        return idNum++;
//    }


    public void findMaxId(){
        Long id = saveCoinStateRepository.findMaxId();
        if(id==null){
            System.out.println("null");
        }

        System.out.println(id);
    }
    //1분짜리
//    @Async
//    @Scheduled(fixedRate = 60000)
//    public synchronized void OneMinute() throws InterruptedException{
//        //synchronized 사용하면 이전작업이 끝나기 전까지 록 걸어서 대기 시킨다함.
//        oneM.saveCoinPrice(executor);
//    }
//    //3분짜리
//    @Async
//    @Scheduled(fixedRate = 180000)
//    public void ThreeMinute() throws InterruptedException{
//        threeM.saveCoinPrice(executor);
//    }
//    //5분짜리
//    @Async
//    @Scheduled(fixedRate = 300000)
//    public void FiveMinute() throws InterruptedException{
//        fiveM.saveCoinPrice(executor);
//    }
//    //24시간짜리
//    @Async
//    @Scheduled(fixedRate = 86400000)
//    public void Daily() throws InterruptedException{
//        daily.saveCoinPrice(executor);
//    }

}