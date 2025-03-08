package com.example.coin;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class BithumbApi {
    public String getResponseData(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    public String currentPrice(String coinName) throws IOException, JSONException {
        String closingPrice = "";
        String url = "https://api.bithumb.com/public/ticker/";
        url += coinName;
        url+= "_KRW";
        String responseData = getResponseData(url);
        JSONObject jsonObject = new JSONObject(responseData);
        JSONObject jsonObject2 =    new JSONObject(jsonObject.get("data").toString());
        closingPrice = jsonObject2.get("closing_price").toString();
        return closingPrice;
    }
}
