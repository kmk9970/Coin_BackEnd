package com.example.coin;

import com.example.coin.service.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class designAPI { //마이페이지 api
    private  final interestService interestService;
    private  final TradeService tradeService;
    private  final TransactionHistoryService service;
    private Map<String,List<String>> myPage;
    private   String userId ="";
//    @GetMapping(value = "/drill")
//    public String test(){
//        return "test";
//

    @GetMapping (value =  "/myPageApi")
    public Map<String,List<String>> adas(HttpServletRequest request) throws JSONException, IOException {
        HttpSession session = request.getSession();
        userId = String.valueOf(session.getAttribute("ID"));
        //userId= "조승빈";
        List<TransactionHistory> tradeInfo = service.getUserTrade(userId);
        double trade_cnt = 0.0 ; // 총 매매 횟수
        double win_cnt = 0.0;
        myPage = new HashMap<>();
        //service.
        List<TransactionHistory> userOwnCoin= new ArrayList<>();
//        for(int i= 0;i<tradeInfo.size();i++){
//            coinList.add(tradeInfo.get(i).getCoinName());
//        }


        for(int idx = 0;idx < tradeInfo.size();idx++ ){
            if(tradeInfo.get(idx).getState().equals("매수")){
                userOwnCoin.add(tradeInfo.get(idx));
                trade_cnt++;
            }
            else if (tradeInfo.get(idx).getState().equals("매도")) {
                for(int i = 0 ; i<userOwnCoin.size();i++){
                    if(tradeInfo.get(idx).getCoinName().equals(userOwnCoin.get(i).getCoinName())){
                        String sellPrice  = tradeInfo.get(idx).getAfterTrade().substring(1);
                        String buyPrice = userOwnCoin.get(i).getAfterTrade().substring(1);
                        if(Double.valueOf(sellPrice)>Double.valueOf(buyPrice)){
                           win_cnt++;
                        }
                        userOwnCoin.remove(i);
                        break;
                    }
                }
            }
        }

        List<String> myCoin = new ArrayList<>();
        List<user_info>  user = service.getUserData(userId);
        getUserCash uC = new getUserCash(user.get(0).getApi_key(),user.get(0).getSec_key());
        String total_krw = uC.getCash(); //유저가 가진 현금량
        Double myAllAsset = 0.0; //내 총자산 (내가 보유한 모든 코인가격 누적합 + 현금);
        List<transaction_log> userAmount = tradeService.getUserAmount(userId);
        for(int i = 0;i< userAmount.size();i++){
            myAllAsset+= Double.valueOf(userAmount.get(i).getAmount()) *Double.valueOf(userAmount.get(i).getCoin_price());
        }

        for(int i = 0 ; i< userOwnCoin.size();i++){
            myAllAsset += Double.valueOf(userOwnCoin.get(i).getAmount()) * Double.valueOf(userOwnCoin.get(i).getPrice());
        }
        myAllAsset += Double.valueOf(total_krw);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        for(int i = 0 ; i< userOwnCoin.size();i++){
            myCoin.add(userOwnCoin.get(i).getCoinName());
            Double coinAmount = Double.valueOf(userOwnCoin.get(i).getAmount()) * Double.valueOf(userOwnCoin.get(i).getPrice());
            coinAmount = coinAmount/myAllAsset;
            //소수점 첫째자리까지만 짜름
            myCoin.add(decimalFormat.format(coinAmount));
        }

        myCoin.add("KRW");
        myCoin.add(decimalFormat.format(Double.valueOf(total_krw)/myAllAsset));
        for(int i = 0;i< userAmount.size();i++){
            myCoin.add(userAmount.get(i).getCoin_name());
            Double tmp = Double.valueOf(userAmount.get(i).getAmount()) *Double.valueOf(userAmount.get(i).getCoin_price());
            tmp /=myAllAsset;
            myCoin.add(String.valueOf(tmp));
        }

        myPage.put("coinList",myCoin);
        List<String> win_rate = new ArrayList<>();
        win_rate.add(String.valueOf(win_cnt/trade_cnt*100));
        myPage.put("win_rate",win_rate);
        List<String> trade_rate = new ArrayList<>();
        myPage.put("trade_rate",trade_rate);
        List<String> user_amount = new ArrayList<>();
        user_amount.add(service.getUserRank(userId).get(0).getUser_amount());
        myPage.put("user_amount",user_amount);
        System.out.println("정답: 수익률: "+ getProfit());
        List<interest> interests = interestService.getUserInterest(userId);
        List<String>  userInterest = new ArrayList<>();
        for(int i=0; i<interests.size();i++){
            userInterest.add(interests.get(i).getCoinName());
            System.out.println("관심종목: "+interests.get(i).getCoinName());
        }
        myPage.put("userInterest",userInterest);
        List<String> userCash = new ArrayList<>();
        userCash.add(total_krw);
        myPage.put("userCash",userCash); //유저가 가진 현금량
        return myPage;
    }
    public void insertValue(List<TransactionHistory> e,HashMap<String, Double> h){ //해시테이블도 하나 받아와야함
        String temp;
        for(int i=0;i<e.size();i++){ //해시테이블에 값 넣기
            if(h.containsKey(temp=e.get(i).getCoinName())){ //있으면 업데이트
                h.replace(temp,h.get(temp)+Double.valueOf(
                        e.get(i).getAfterTrade()
                                .replace(",","")
                                .replace(" KRW","")
                                .replace("+","")
                                .replace("-","")));
            }
            else {
                h.put(e.get(i).getCoinName(), Double.valueOf( //없으면 생성
                        e.get(i).getAfterTrade()
                                .replace(",", "")
                                .replace(" KRW", "")
                                .replace("+", "")
                                .replace("-", "")
                ));
            }
        }
    }

    //수익률을 알려주는 함수
    public String getProfit(){ //(처음현금+팔아서 얻은 금액 - 살때 쓴 금액)/처음 가지고 있던 현금*100

        HashMap<String,Double> buy=new HashMap<String,Double>(); //매수
        HashMap<String,Double> sell=new HashMap<String,Double>(); //매도
        insertValue(service.GetexcelData("매수"),buy);//state가 매수인 애들만 불러오기
        insertValue(service.GetexcelData("매도"),sell); //state가 매도인 애들만 불러오기
        List<String> key=new ArrayList(buy.keySet());
        Double total_profit=0.0;
        Double total_invest=0.0;
        for (int i=0;i<key.size();i++){
            if((sell.get(key.get(i))!=null)&(buy.get(key.get(i))!=null)){
                Double profit=sell.get(key.get(i)) - buy.get(key.get(i));
                System.out.println(key.get(i) + ":" + profit);
                total_profit+=profit;
                total_invest+=buy.get(key.get(i));
            }
            else{
                total_invest+=buy.get(key.get(i));
                continue;
            }
        }
        System.out.println("total profit:"+total_profit);
        System.out.println("total invest:"+total_invest);
        double temp=(-total_profit-total_invest)/total_invest*100;
        System.out.println("total profit rate:"+temp+"%");

        return String.valueOf(temp);
    }
}
