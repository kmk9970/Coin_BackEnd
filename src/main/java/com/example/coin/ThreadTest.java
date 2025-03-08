package com.example.coin;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ThreadTest {


    public static double calculateAverage(List<Double> values, int period) {
        if (values.size() < period) {
            return 0.0;
        }

        double sum = 0.0;
        for (int i = values.size() - period; i < values.size(); i++) {
            sum += values.get(i);
        }

        return sum / period;
    }
    public static List<Double> AcalculateRsi(List<Double> closingPrices, int period) {
        List<Double> rsiValues = new ArrayList<>();

        for (int i = 0; i < closingPrices.size(); i++) {
            if (i < period) {
                rsiValues.add(null); // RSI 값이 없는 경우에 null로 표시
                continue;
            }

            List<Double> closes = closingPrices.subList(i - period, i);
            List<Double> changes = new ArrayList<>();

            for (int j = 1; j < closes.size(); j++) {
                double change = closes.get(j) - closes.get(j - 1);
                changes.add(change);
            }

            List<Double> gains = new ArrayList<>();
            List<Double> losses = new ArrayList<>();

            for (Double change : changes) {
                if (change > 0) {
                    gains.add(change);
                    losses.add(0.0);
                } else {
                    gains.add(0.0);
                    losses.add(-change);
                }
            }

            double avgGain = calculateAverage(gains, period);
            double avgLoss = calculateAverage(losses, period);

            if (avgLoss == 0.0) {
                rsiValues.add(100.0);
            } else {
                double rs = avgGain / avgLoss;
                double rsi = 100.0 - (100.0 / (1.0 + rs));
                rsiValues.add(rsi);
            }
        }

        return rsiValues;
    }
    public static double calculateRsi(List<Double> prices, int period) {
        if (prices == null || prices.size() < period) {
            throw new IllegalArgumentException("Invalid input data or period");
        }

        // RSI 계산을 위한 변수 초기화
        double gainSum = 0.0;
        double lossSum = 0.0;

        // 가격 변동 계산
        for (int i = 1; i < period; i++) {
            double priceDiff = prices.get(i) - prices.get(i - 1);
            if (priceDiff > 0) {
                gainSum += priceDiff;
            } else {
                lossSum -= priceDiff; // 음수를 양수로 바꿈
            }
        }

        // 평균 수익과 평균 손실 계산
        double avgGain = gainSum / period;
        double avgLoss = lossSum / period;

        // RSI 계산
        double relativeStrength = avgGain / avgLoss;
        double rsi = 100 - (100 / (1 + relativeStrength));

        return rsi;
    }
    public static void getCoinPrice(String coinName)  {
        String url  = "https://api.bithumb.com/public/candlestick/";
        url+=coinName+"_KRW/24h";
    try {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .build();

            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();


            JSONObject json = new JSONObject(body.string());
            JSONArray data =  json.getJSONArray("data");
            //String data = json.getString("data");
            List<Double> coinPriceList = new ArrayList<>();
            for (int x = 0;x<data.length();x++){
                Instant  instant  = Instant.ofEpochMilli((Long) data.getJSONArray(x).get(0));
                String coinDate = DateTimeFormatter.ofPattern("yyyyMMdd")
                        .withZone(ZoneOffset.UTC)
                        .format(instant);
//                    System.out.println("기준 시간: "+coinDate);
//                    System.out.println("시가: "+ data.getJSONArray(x).get(1));
//                    System.out.println("종가: "+ data.getJSONArray(x).get(2));
//                    System.out.println("고가: "+ data.getJSONArray(x).get(3));
//                    System.out.println("저가: "+ data.getJSONArray(x).get(4));
//                    System.out.println("거래량: "+ data.getJSONArray(x).get(5));
                    Double d = Double.valueOf((String) data.getJSONArray(x).get(2));
                    coinPriceList.add(d);
            }

        //Collections.reverse(coinPriceList);  //배열 역순
        Double rsi = calculateRsi(coinPriceList,14);

        System.out.println(coinName+"의 rsi: "+ rsi);


        }catch (Exception e){
            System.out.println("오류" + e);
        }
    }




    public static void main(String[] args) throws JSONException, IOException {

//        AllCoin a = new AllCoin( "https://api.bithumb.com/public/ticker/ALL_KRW");
//        List <String> cN = a.allCoin(); //상장된 모든 코인 이름
//        long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
//        for(int i =0; i<cN.size();i++){
//            getCoinPrice(cN.get(i));
//        }
//        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
//        long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
//        System.out.println("시간차이(m) : "+secDiffTime);

    }
}

