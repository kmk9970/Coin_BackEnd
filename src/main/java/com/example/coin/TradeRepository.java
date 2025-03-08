package com.example.coin;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TradeRepository {
    private final EntityManager EM;
    public List<transaction_log> getBuyPrice(String coin_name, String user_id){
        return EM.createQuery("select e from transaction_log e where e.user_id=:user_id and e.coin_name=:coin_name", transaction_log.class)
                .setParameter("user_id",user_id)
                .setParameter("coin_name",coin_name)
                .getResultList();
    }
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
    public List<transaction_log> getUserAmount(String user_id){
        return EM.createQuery("select e from transaction_log e where e.user_id=:user_id").setParameter("user_id",user_id).getResultList();
    }
    public transaction_log getCoinAmount(String coin_name, String user_id){ //사용자의 아이디와 코인이름을 입력받아서 코인보유량을 얻는 함수
        List<transaction_log> log=EM.createQuery("select e from transaction_log e where e.user_id=:user_id and e.coin_name=:coin_name", transaction_log.class)
                .setParameter("user_id",user_id)
                .setParameter("coin_name",coin_name)
                .getResultList();
        if (!log.isEmpty()) {
            return log.get(0);
        }
        else{
            return null;
        }
    }

    public void WriteLog(transaction_log log){ //보유량의 신규작성
        EM.persist(log);
    }

    public void EditLog(transaction_log log){ //코인 보유량을 고치는 함수
        if(log.getAmount()<=0){ //받은 보유량이 0이하라면 삭제
            EM.remove(log);
        }
        else{EM.merge(log);} //아니라면 데이터베이스에 반영
    }

    public  String buy_coin(Api_Client api, String coinName,double price) throws IOException, JSONException, JSONException {
        String coinPrice = getCoinPrice(coinName);
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("currency", coinName);
        String result1 = api.callApi("/info/balance", rgParams);
        //System.out.println(result);
        JSONObject json = new JSONObject(result1);
        String data = json.getString("data"); //string()쓰기

        JSONObject json1 = new JSONObject(data);
        String total_krw = json1.getString("total_krw").toString();  //보유 자산 구하기

        if( Double.parseDouble(total_krw)<price){
            return "0";
        }

        Double unit = price/Double.parseDouble(coinPrice)*0.69;
        if(Double.parseDouble(coinPrice)*unit<=1000) {
            return"0";
        }
        if(Double.parseDouble(coinPrice)*unit>Double.parseDouble(total_krw)*0.69&Double.parseDouble(total_krw)<500){
            return "0";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.####");

        // 잘라내기
        String formattedNumber = decimalFormat.format(unit);

        HashMap<String, String> rgParams1 = new HashMap<String, String>();
        rgParams1.put("units", formattedNumber); //소수점 4자리 맞추기=코인수량
        rgParams1.put("order_currency", coinName); //매수 하려는 코인 이름
        rgParams1.put("payment_currency", "KRW"); // 매수하려는 통화
        String result = api.callApi("/trade/market_buy", rgParams1);
        System.out.println("결과"+ result);
        return formattedNumber;
    }
    public int sell_coin(Api_Client api,String coinName,String unit) throws IOException {
        try {
            HashMap<String, String> rgParams = new HashMap<String, String>();
            rgParams.put("units", unit); //소수점 4자리 맞추기
            rgParams.put("order_currency", coinName); //매도 하려는 코인 이름
            rgParams.put("payment_currency", "KRW"); //매도하려는 통화
            String result = api.callApi("/trade/market_sell", rgParams);
            return 1;
        } catch (Exception e) {
            System.out.println("에러 " + e);
        }
        return 0;
    }
}
