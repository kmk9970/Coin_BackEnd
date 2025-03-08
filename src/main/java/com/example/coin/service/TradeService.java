package com.example.coin.service;

import com.example.coin.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private  final TransactionHistoryRepository transactionReposi;
    Api_Client api=new Api_Client("e8d62d019ec15f02480fff030e52db42","eddf5053c29a30136cc954f41d49df65");
    public void buyCoin(String coinName, String price) throws JSONException, IOException {

        tradeRepository.buy_coin(api,coinName,Double.parseDouble(price));
    }
    public void sellCoin(String coinName, String units) throws IOException {
        tradeRepository.sell_coin(api,coinName,units);
    }
    public int WritePurchaseLog(String coin_name, String price,String user_id) throws JSONException, IOException {
        List<user_info> user=transactionReposi.getUserData(user_id); //유저찾기
        Api_Client api=new Api_Client(user.get(0).getApi_key(),user.get(0).getSec_key());
        transaction_log log=new transaction_log();
        transaction_log past_log=tradeRepository.getCoinAmount(log.getCoin_name(), log.getUser_id());
        log.setAmount(Double.parseDouble(tradeRepository.buy_coin(api,coin_name,Integer.parseInt(price))));
        if(log.getAmount()!=0){
            if(past_log==null) {//만약 코인보유량이 없으면 새로생성
                log.setCoin_name(coin_name);
                log.setUser_id(user.get(0).getIdentity());
                log.setCoin_price(tradeRepository.getCoinPrice(coin_name));
                tradeRepository.WriteLog(log);
            }
            else{//있다면 보유량을 더해서 업데이트
                past_log.setAmount(past_log.getAmount()+log.getAmount());
                past_log.setCoin_price(tradeRepository.getCoinPrice(coin_name));
                tradeRepository.EditLog(past_log);
            }
            return 1; //구매 성공시 1리턴
        }
        return 0; //구매 실패시 0리턴
    }

    public int WriteSellLog(String coin_name, String price,String user_id) throws IOException, JSONException {
        List<user_info> user=transactionReposi.getUserData(user_id); //유저찾기
        transaction_log log=new transaction_log(); //로그객체 생성
        Api_Client api=new Api_Client(user.get(0).getApi_key(),user.get(0).getSec_key()); //api생성
        DecimalFormat decimalFormat = new DecimalFormat("#.####"); //구매를 위한 수량 포맷 맞추기
        //String coinPrice=tradeRepository.getCoinPrice(coin_name); //코인가격 받아오기// 이거 고치기
        //코인가격이 현재 실시간 가격이 아니라 매수 했을 때 가격을 불러와야함
        String coinPrice = tradeRepository.getBuyPrice(coin_name,user_id).get(0).getCoin_price();
        String amount=decimalFormat.format(Double.valueOf(price)*0.68/Double.parseDouble(coinPrice)); //판매할 수량 결정
        System.out.println("팔 현금량 " +Double.valueOf(price));
        System.out.println("현재 코인가격 " +Double.parseDouble(coinPrice));
        System.out.println("현재 팔 량"+amount);

        log.setAmount(Double.parseDouble(amount));
        //log.setCoin_price(tradeRepository.getCoinPrice(coin_name));
        transaction_log past_log=tradeRepository.getCoinAmount(coin_name,user.get(0).getIdentity()); //테이블에서 코인기록 불러오기
        System.out.println("현재 보유량"+past_log.getAmount());
        if(past_log.getAmount()<log.getAmount()||past_log==null){
            System.out.println("개 ㅅㅂ");
            return 0; //코인 보유량 보다 판매하려는 양이 많거나 보유한 코인이 없으면 리턴
        }

        if(tradeRepository.sell_coin(api,coin_name,amount)!=0) {
            log.setCoin_name(coin_name);
            past_log.setAmount(past_log.getAmount()-log.getAmount());
            //past_log.setCoin_price(tradeRepository.getCoinPrice(coin_name));
            tradeRepository.EditLog(past_log);
            return 1; //판매성공시 1 리턴
        }
        else return 0; //판매 실패시 0리턴
    }
    public List<transaction_log> getUserAmount(String user_id){ return tradeRepository.getUserAmount(user_id);}

}