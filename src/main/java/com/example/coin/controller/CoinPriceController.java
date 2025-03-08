package com.example.coin.controller;


import com.example.coin.CoinPatternMatcher;
import com.example.coin.TimestampConverter;
import com.example.coin.dto.CoinPriceDto;
import com.example.coin.entity.CoinInfo;
import com.example.coin.service.CoinPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class CoinPriceController {
    private final CoinPriceService coinPriceService;
    @GetMapping (value = "/coin_current_price_list")
    public ResponseEntity<?>getCurrentPriceList(@RequestParam("state")String state,
                                                @RequestParam("num")int num){
        return new ResponseEntity<>(coinPriceService.getCurrentPriceList(state,num),HttpStatus.OK);
    }

    @GetMapping (value = "/coin_price") //어떤 기간동안 해당 코인의 가격리스트
    public ResponseEntity<?> getCoinPrice(  @RequestParam("coinName") String coinName,
                                                            @RequestParam("coinState") String coinState,
                                                            @RequestParam("startDate") String startDate,
                                                            @RequestParam("endDate") String endDate   ) throws ParseException {

        if (coinName == null || coinName.isEmpty()) {
            return ResponseEntity.badRequest().body("coinName parameter is missing or empty");
        }
        if (coinState == null || coinState.isEmpty()) {
            return ResponseEntity.badRequest().body("coinState parameter is missing or empty");
        }
        if (startDate == null || startDate.isEmpty()) {
            return ResponseEntity.badRequest().body("startDate parameter is missing or empty");
        }
        if (endDate == null || endDate.isEmpty()) {
            return ResponseEntity.badRequest().body("endDate parameter is missing or empty");
        }
        return new ResponseEntity<>(coinPriceService.getCoinInfo(coinName,coinState, startDate,endDate), HttpStatus.OK);
    }


    @GetMapping(value = "/coin_price_test")
    public void  we(){
        List<Map<String, Object>> data = new ArrayList<>();
        CoinPatternMatcher coinPatternMatcher = new CoinPatternMatcher();
        TimestampConverter timestampConverter = new TimestampConverter();

        long startTime = System.currentTimeMillis();
        List<CoinInfo> coinPrices=   coinPriceService.getCoinPrice();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("조회 시간" + totalTime + " ms");
        for(CoinInfo coinPrice : coinPrices){
            String coin = coinPrice.getCoinName();
            String date = timestampConverter.stampToDate(coinPrice.getCoinDate());
            double open =  Double.parseDouble(coinPrice.getOpeningPrice());
            double high =  Double.parseDouble(coinPrice.getMaxPrice());
            double  low  =  Double.parseDouble(coinPrice.getMinPrice());
            double close = Double.parseDouble(coinPrice.getClosingPrice());
            double volume = Double.parseDouble(coinPrice.getUnitsTraded());
            data.add(Map.of("coin",coin , "date", date,"open",open,
                    "high",high,"low",low,
                    "close", close,"volume",volume));
        }
        long start = System.currentTimeMillis();

//        data.add(Map.of(
//                "coin", coin,
//                "date", date.toString(),
//                "open", open,
//                "high", high,
//                "low", low,
//                "close", close,
//                "volume", volume
//        ));

        coinPatternMatcher.findSimilarPatterns(data,"2024-03-12","2024-03-12","XLM");
        long end = System.currentTimeMillis();
        long total = end - start;
        System.out.println("분석 시간" + total + " ms");
    }
}
