package com.example.coin.service;

import com.example.coin.bithumb.UserAsset;
import com.example.coin.dto.UserAssetDto;
import com.example.coin.entity.CoinName;
import com.example.coin.repository.CoinNameRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAssetService {
    String apiKey = "e8d62d019ec15f02480fff030e52db42";
    String apiSec = "eddf5053c29a30136cc954f41d49df65";
    UserAsset cash = new UserAsset(apiKey,apiSec);
    private  final CoinNameRepository coinNameRepository;
    public List<UserAssetDto> getUserAsset() throws JSONException, IOException {
        String userAsset =  cash.getUserAsset();
        JSONObject json = new JSONObject(userAsset);
        List<CoinName> coinNameList =coinNameRepository.findAll();
        List<UserAssetDto> userAssetDtoList = new ArrayList<>();
        String query ="total_";
        String userCash = json.getString("total_krw" );
        for (CoinName coinName: coinNameList){
            String coin=  coinName.getCoinName();
            Double amount = Double.parseDouble(json.getString(query+coin.toLowerCase()).toString());  //보유 자산 구하기
            //
            if(amount>0.0){
                UserAssetDto userAssetDto = UserAssetDto.builder().
                        coinAmount(String.valueOf(amount)).
                        //tradePrice().
                        coinName(coin).
                        cash(userCash).
                        build();
                //System.out.println(coinName.getKoreanName());
                userAssetDtoList.add(userAssetDto);
            }
        }
        return userAssetDtoList;
    }
}
