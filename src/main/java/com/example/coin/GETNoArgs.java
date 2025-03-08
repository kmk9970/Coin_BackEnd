package com.example.coin;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GETNoArgs {

    public static void main(String[] args) throws IOException, JSONException {

//        String url1 = "https://api.bithumb.com/v1/candles/minutes/1?market=KRW-BTC&count=200";
//        AllCoin allCoin1 = new AllCoin(url1);
//        System.out.println(allCoin1.getData());
       OkHttpClient client = new OkHttpClient();
//        String url = "https://api.bithumb.com/v1/market/all?isDetails=false";
//        Request request = new Request.Builder()
//                .url(url)
//                .get()
//                .addHeader("accept", "application/json")
//                .build();
//
//        Response response = client.newCall(request).execute();
//        ResponseBody body = response.body();
//
//        JSONArray json = new JSONArray(body.string());
//        for(int i  = 0 ;i< json.length();i++){
//            JSONObject jsonObject = new JSONObject(json.get(i).toString());
//            String tempParse[] = jsonObject.getString("market").split("-");
//            System.out.println("coin: "+tempParse[1]);
//        }

//        String url = "https://api.bithumb.com/public/ticker/ALL_KRW";
//        AllCoin allCoin = new AllCoin(url);
//        JSONArray dataJson = new JSONArray(allCoin.getData());
//            for (int i = 0; i < dataJson.length(); i++) {
//                System.out.println(dataJson.get(i));
//            }


        String state[]  = {"1m","3m","5m","10m","15m","30m","1h","4h","6h","12h","24h","1w","1mm"};
        int cnt = 0;
        for (String s:state) {
            String url = "https://api.bithumb.com/public/candlestick/BTC_KRW/";
            Request request = new Request.Builder()
                    .url(url+s)
                    .get()
                    .addHeader("accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            JSONObject json = new JSONObject(body.string());

            JSONArray dataJson = new JSONArray(json.get("data").toString());
            for (int i = 0; i < dataJson.length(); i++) {
                cnt ++;
                JSONArray rowData = new JSONArray(dataJson.get(i).toString());
                //기준기간,시가,종가,고가,저가,거래량
                System.out.println(rowData.get(0));
            }
        }
        System.out.println("총개수 : "+ cnt);


//        Response response = client.newCall(request).execute();
//        String responseBody = response.body().string();
//        JSONArray jsonArray = new JSONArray(responseBody);
//        System.out.println("길이: "+jsonArray.length());
//        for (int i = 0 ;i<jsonArray.length(); i++){
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//        }
        // 첫 번째 요소만 추출


        // 첫 번째 요소만 추출
        // 데이터를 저장할 Map 생성
//        Map<String, Object> resultMap = new HashMap<>();
//        resultMap.put("market", jsonObject.getString("market"));
//        resultMap.put("candle_date_time_utc", jsonObject.getString("candle_date_time_utc"));
//        resultMap.put("candle_date_time_kst", jsonObject.getString("candle_date_time_kst"));
//        resultMap.put("opening_price", jsonObject.getDouble("opening_price"));
//        resultMap.put("high_price", jsonObject.getDouble("high_price"));
//        resultMap.put("low_price", jsonObject.getDouble("low_price"));
//        resultMap.put("trade_price", jsonObject.getDouble("trade_price"));
//        resultMap.put("timestamp", jsonObject.getLong("timestamp"));
//        resultMap.put("candle_acc_trade_price", jsonObject.getDouble("candle_acc_trade_price"));
//        resultMap.put("candle_acc_trade_volume", jsonObject.getDouble("candle_acc_trade_volume"));
//        resultMap.put("unit", jsonObject.getInt("unit"));
//
//        // Map 출력
//        System.out.println(resultMap);
    }
}
