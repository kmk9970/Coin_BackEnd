//package com.example.coin.service;
//
//import com.example.coin.BithumbApi;
//import com.example.coin.Indicators;
//import com.example.coin.service.TradeService;
//import com.example.coin.entity.CoinName;
//import com.example.coin.repository.CoinNameRepository;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
//import com.squareup.okhttp.ResponseBody;
//import lombok.RequiredArgsConstructor;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
///
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class AutoTrading {
//
//    private final TradeService tradeService;
//    private  final CoinNameRepository coinNameRepository;
//
//    @Scheduled(fixedRate = 1000000000) //1초에 한번씩
//    public void autoTradingSystem() throws IOException, JSONException {
//        BithumbApi bithumbApi = new BithumbApi();
//        List<CoinName> coinNameList  = coinNameRepository.findAll();
//        while (true) {
//            String buyCoinName = "";
//            String units = "";
//            double buyPrice = 0.0;
//            while (buyCoinName.equals("")) {
//                int stopFlag = 0;
//
//                for (CoinName coinName : coinNameList) {
//                    if (stopFlag == 1) {
//                        break;
//                    }
//                    List<Double> closingPrices = new ArrayList<>();
//                    String url = "https://api.bithumb.com/public/candlestick/";
//                    url += coinName.getCoinName();
//                    url += "_KRW/1m";
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder()
//                            .url(url)
//                            .get()
//                            .addHeader("accept", "application/json")
//                            .build();
//                    Response response = client.newCall(request).execute();
//
//                    String responseData = response.body().string();
//                    JSONObject jsonObject = new JSONObject(responseData);
//                    JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONArray rowData = jsonArray.getJSONArray(i);
//                        closingPrices.add(Double.parseDouble(rowData.get(2).toString()));
//                    }
//                    Indicators indicators = new Indicators(closingPrices);
//                    double RSI = indicators.calculateRSI(14);
//                    double lowerBand = indicators.calculateBollingerBands(20, 2);
//                    // if ((RSI<30)&&(closingPrices.get(closingPrices.size()-1)<lowerBand))
//                    if ((RSI < 30) && (closingPrices.get(closingPrices.size() - 1) < lowerBand)) {
//                        buyCoinName = coinName.getCoinName();
//                        tradeService.buyCoin(buyCoinName, "90000");
//                        stopFlag = 1;
//                        buyPrice = closingPrices.get(closingPrices.size() - 1);
//                        DecimalFormat decimalFormat = new DecimalFormat("#.####");
//                        units = decimalFormat.format(buyPrice / 90000 * 0.69);
//                        System.out.println("매수 가격: "+ buyPrice);
//                    }
//                    System.out.println("coin 이름: " + coinName.getCoinName() + " RSI: " + RSI + " 볼린저 하단: " + lowerBand);
//                }
//            }
//            while (!buyCoinName.equals("")) {
//                double currentPrice = Double.parseDouble(bithumbApi.currentPrice(buyCoinName));
//                if ((currentPrice >= buyPrice * 1.015) || (currentPrice <= buyPrice * 0.97)) {
//                    tradeService.sellCoin(buyCoinName, units);
//                    buyCoinName = "";
//                    break;
//                }
//            }
//        }
//    }
//}
