package com.example.coin.bithumb;

import com.example.coin.Api_Client;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class UserAsset {
    private final  Api_Client api ;
    public  UserAsset(String key, String sec){
        api= new Api_Client(key,sec);
    }
    //현재 사용자가 가진 현금량을 리턴
    public String getUserAsset() throws JSONException, IOException {
        String coinName = "All";
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("currency", coinName);
        String result1 = api.callApi("/info/balance", rgParams);
        JSONObject json = new JSONObject(result1);
        String data = json.getString("data"); //string()쓰기
        return data;

    }
}
