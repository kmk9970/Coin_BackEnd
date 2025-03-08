package com.example.coin;

import com.example.coin.entity.CoinName;
import com.example.coin.entity.CoinPrice;
import com.example.coin.entity.CoinInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionHistoryService {
    private  final TransactionHistoryRepository transactionReposi;
    public void delCoinName(){
        transactionReposi.delCoinName();
    }
    public void delCoinInfo(){
        transactionReposi.delCoinInfo();
    }
    public List<user_info> loginCheck (String identity, String pass){
        return transactionReposi.loginCheck(identity, pass);
    }
    public void excelDataSave(TransactionHistory t){
        transactionReposi.excelDataSave(t);
    }

    public  void registMember(user_info u){
        transactionReposi.registMember(u);
    }
    public  List<CoinName> getAllCoinName() {return transactionReposi.getAllCoinName();}
    public List<user_info> getAllUserData() {return transactionReposi.getAllUserData();}
    public List<user_info>  getUserData(String id){
        return transactionReposi.getUserData(id);
    }

    public int getCoinSize(){ return transactionReposi.getCoinSize();}
    public void save(CoinName t){
        transactionReposi.save(t);
    }
    public  void coinStateSave(CoinInfo c){
        transactionReposi.coinStateSave(c);
    }
    public  List<CoinInfo> getCoinState(){
       return transactionReposi.getCoinState();
    }
    public  void dumSave (CoinPrice c) {transactionReposi.dumSave(c);}
    public  void saveCoinPrice(List<CoinPrice> c){transactionReposi.saveCoinPrice(c);}
    public void bulkCoinPrice (List<CoinPrice> c) {transactionReposi.bulkCoinPrice(c);}
    public List<CoinPrice> getCoinPrice(String coinName){
        return transactionReposi.getCoinPrice(coinName);
    }

    public List<TransactionHistory> getUserTrade(String user){
        return  transactionReposi.getUserTrade(user);
    }
    public List<String> coinStatistics() {return transactionReposi.coinStatistics();}

    public List<TransactionHistory> GetexcelData(String type){
        return transactionReposi.getDatafromExcel(type);
    }
    public void registUserRanking(user_rank uR){ transactionReposi.registUserRanking(uR);}
    public List<user_rank> getUserRank(String userId) { return transactionReposi.getUserRank(userId);}
    public List<user_rank> getRank() {return  transactionReposi.getRank();}

}
