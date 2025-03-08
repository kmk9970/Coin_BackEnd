package com.example.coin;


import com.example.coin.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class statisticsApi {
    private   String userId ="";
    private  final TradeService tradeService;
    private  final TransactionHistoryService service;
    Map<String,List<String>> api = new HashMap<>();
    @GetMapping(value =  "/login/statisticsApi")
    public Map<String, List<String>> makeAPI(){

        //사용자들이 매매한 코인중 누적 매매량을 보여줌
        List<String> coinCount =  service.coinStatistics();

        api.put("coinStatistics",coinCount);
        List<user_rank> userRank = service.getRank();
        List<String> userRankAPI = new ArrayList<>();
        for(int i = 0;i<userRank.size();i++){
            userRankAPI.add(userRank.get(i).getUser_id());
            String winR = userRank.get(i).getWin_rate();
            if(winR.length()>3){
                winR = winR.substring(0,4);
            }
            userRankAPI.add(winR);
        }
        api.put("userRank",userRankAPI);
        return api;
    }

}
