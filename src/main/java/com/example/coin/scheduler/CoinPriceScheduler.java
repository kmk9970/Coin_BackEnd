package com.example.coin.scheduler;

import com.example.coin.repository.CoinInfoRepository;
import com.example.coin.repository.CoinNameRepository;
import com.example.coin.repository.SaveCoinStateRepository;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class CoinPriceScheduler {
    AtomicLong idNum;
    OkHttpClient client = new OkHttpClient();
    CoinNameRepository coinNameRepository;
    CoinInfoRepository  coinInfoRepository;;
    String url = "https://api.bithumb.com/public/candlestick/";
    List<String> coinNameList;
    String state;
    SaveCoinStateRepository repository;

    public CoinPriceScheduler(SaveCoinStateRepository repository, String state, AtomicLong idNum,CoinInfoRepository coinInfoRepository,CoinNameRepository coinNameRepository) {
        this.coinNameList=repository.getAllCoinName();
        this.state=state;
        this.repository=repository;
        this.idNum=idNum;
        this.coinInfoRepository = coinInfoRepository;
        this.coinNameRepository = coinNameRepository;
    }

    public void saveCoinPrice(ExecutorService executor) throws InterruptedException {
        System.out.println(state + " insert start");
        long startTime = System.currentTimeMillis();

        String sqlTemplate = "INSERT INTO coin_info (id, coin_date, opening_price, closing_price, max_price, min_price, units_traded, coin_name, state) VALUES ";

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String c : coinNameList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    StringBuilder localSql = new StringBuilder(sqlTemplate);

                    Request request = new Request.Builder()
                            .url(url + c + "_KRW/" + state)
                            .get()
                            .addHeader("accept", "application/json")
                            .build();

                    Response response = client.newCall(request).execute();

                    ResponseBody body = response.body();
                    JSONObject json = new JSONObject(body.string());
                    JSONArray dataJson = new JSONArray(json.get("data").toString());

                    for (int i = 0; i < dataJson.length(); i++) {
                        JSONArray rowData = dataJson.getJSONArray(i);
                        String coin_date = rowData.get(0).toString();
                        if (coinInfoRepository.checkDuplicated(coin_date, c) == 1) {
                            continue;
                        }
                        long currentId = idNum.getAndIncrement();
                        localSql.append("(")
                                .append(currentId).append(", '")
                                .append(rowData.get(0).toString()).append("', '")
                                .append(rowData.get(1).toString()).append("', '")
                                .append(rowData.get(2).toString()).append("', '")
                                .append(rowData.get(3).toString()).append("', '")
                                .append(rowData.get(4).toString()).append("', '")
                                .append(rowData.get(5).toString()).append("', '")
                                .append(c).append("', '")
                                .append(state)
                                .append("'),");
                    }

                    if (localSql.length() > sqlTemplate.length()) {
                        String finalSql = localSql.substring(0, localSql.length() - 1);
                        repository.saveCoinInfo(finalSql);
                    }
                } catch (Exception e) {
                    coinNameRepository.deleteByCoinName(c);
                    e.printStackTrace();
                }
            }, executor);

            futures.add(future);
        }

        // 모든 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time for " + state + " inserts: " + totalTime + " ms");
    }

}
