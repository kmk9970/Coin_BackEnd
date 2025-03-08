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

public class getUserCash {  //유저가 보유한 현금을 구하는 클래스
    private  Api_Client api ;
    public  getUserCash(String key, String sec){
        api= new Api_Client(key,sec);
    }

    //현재 사용자가 가진 현금량을 리턴
    public String getCash() throws JSONException, IOException {
        String coinName = "BTC";
        String coinPrice = getCoinPrice(coinName);
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("currency", coinName);
        String result1 = api.callApi("/info/balance", rgParams);
        System.out.println("여기: "+result1);
        JSONObject json = new JSONObject(result1);
        String data = json.getString("data"); //string()쓰기

        JSONObject json1 = new JSONObject(data);
        String total_krw = json1.getString("total_krw").toString();  //보유 자산 구하기
        return total_krw;
    }
    //코인의 현재 가격을 불러오는 함수
    public String getCoinPrice(String coinName) throws IOException, JSONException {
        String URL = "https://api.bithumb.com/public/transaction_history/"+coinName+"_KRW";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        JSONObject json = new JSONObject(body.string());
        JSONArray dataArray= json.getJSONArray("data");

        JSONObject json1 = new JSONObject(dataArray.get(dataArray.length()-1).toString());
        String coinPrice = json1.get("price").toString();
        return coinPrice;
    }
}
