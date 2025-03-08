package com.example.coin;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class AllCoin { //get 방식으로 api를 호출하는 클래스
    private String URL = "";
   private String data = "";
   private  List<String> ans ;
    public  AllCoin(String url) throws IOException, JSONException {


        this.URL = url;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(this.URL)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        JSONObject json = new JSONObject(body.string());


        this.data = json.get("data").toString();
    }
    public String getData(){
        return this.data;
    }
    public  List<String> allCoin() throws IOException, JSONException { //
        //모든 코인이름을 불러오는 함수
        JSONObject dataJson = new JSONObject(this.data);
        Iterator jsonKeys=  dataJson.keys();  //json에 모든 키값들을 불러오는 코드(코인 이름이 키값)-->모든 코인 이름 불러오기
        List<String> coinName = new ArrayList<>();
        while(jsonKeys.hasNext())
        {
            String cN = jsonKeys.next().toString();
            coinName.add(cN); // 키 값 저장
        }
//        for(int i = 0 ; i<coinName.size();i++){
//            System.out.println(coinName.get(i));
//        }
        return coinName ;
    }
    public  List<List<String>> getAllCoinState(List<String> cN) throws JSONException {
        List<List<String>> allCoinState = new ArrayList<>();
        JSONObject dataJson = new JSONObject(this.data);
        String [] arr = {"opening_price","max_price","min_price","units_traded",
                "fluctate_24H","fluctate_rate_24H","prev_closing_price","closing_price",
                "units_traded_24H","acc_trade_value_24H","acc_trade_value"};
        for (int i =0;i<cN.size();i++){
            String dJ= dataJson.get(cN.get(i)).toString();
            try {
                JSONObject dJO = new JSONObject(dJ);
                List<String> temp = new ArrayList<>();
                for (int l =0;l < arr.length;l++){
                    temp.add(dJO.get(arr[l]).toString());
                }
                allCoinState.add(temp);
            }catch (Exception e){

            }
        }


        return allCoinState;
    }
    public List<List<String>> getCandleStick() throws JSONException, IOException { //코인의 일별가격을 리턴하는 함수
        List<List<String>> ans = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(this.URL)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        JSONObject json = new JSONObject(body.string());
        JSONArray dataArray= json.getJSONArray("data");
        for(int i = 0 ; i< dataArray.length();i++) {
            List<String> tmp = new ArrayList<>();
            tmp.add(dataArray.getJSONArray(i).get(0).toString());//기준시간
            tmp.add(dataArray.getJSONArray(i).get(1).toString());//시가
            tmp.add(dataArray.getJSONArray(i).get(2).toString());//종가
            tmp.add(dataArray.getJSONArray(i).get(3).toString());//고가
            tmp.add(dataArray.getJSONArray(i).get(4).toString());//저가
            tmp.add(dataArray.getJSONArray(i).get(5).toString());//거래량
            ans.add(tmp);
        }
        return ans;
    }
    public static  void main(String args[]) throws JSONException, IOException {

    }
}
