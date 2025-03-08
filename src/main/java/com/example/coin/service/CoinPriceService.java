package com.example.coin.service;


import com.example.coin.Indicators;
import com.example.coin.TimestampConverter;
import com.example.coin.dto.CoinPriceDto;
import com.example.coin.entity.CoinInfo;
import com.example.coin.dto.CoinInfoDto;
import com.example.coin.entity.CoinName;
import com.example.coin.repository.CoinInfoRepository;
import com.example.coin.repository.CoinNameRepository;
import com.example.coin.repository.CoinPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CoinPriceService {
    private  final CoinPriceRepository coinPriceRepository;
    private final CoinInfoRepository coinInfoRepository;
    private final CoinNameRepository coinNameRepository;
    public List<CoinInfo> getCoinPrice(){
        return coinPriceRepository.getCoinPrice();
    }
    public List<CoinPriceDto>getCurrentPriceList(String state, int num){
        List<CoinPriceDto> coinPriceDtos = new ArrayList<>();
        List<CoinName> coinNameList = coinNameRepository.findAll();
        Pageable pageable = PageRequest.of(0, num+1); // 첫 번째 페이지, num 개만큼 가져옴
        for(CoinName coinName:coinNameList){

            List<Double> closingPriceList = coinInfoRepository.findClosingPricesByCoinNameAndState(coinName.getCoinName(), state, pageable);
            // 값이 num보다 적은 경우 예외 처리
            if (closingPriceList.size() < num ||closingPriceList.size() <  14) {
                continue;
//                throw new IllegalArgumentException("Insufficient data for coin: " + coinName.getCoinName() +
//                        ". Expected at least " + num + " closing prices, but found " + closingPriceList.size());
            }
            Indicators indicators = new Indicators(closingPriceList);
            Double rsi = indicators.calculateRSI(num);
            String currentPrice = String.valueOf(closingPriceList.get(0));
            CoinPriceDto coinPriceDto = CoinPriceDto.builder().
                    currentPrice(currentPrice).
                    coinName(coinName.getKoreanName()).
                    state(state).
                    rsi(rsi).
                    build();

            coinPriceDtos.add(coinPriceDto);

        }
        return coinPriceDtos.stream().sorted(Comparator.comparing(CoinPriceDto::getRsi).reversed()).collect(Collectors.toList());

    }
    public List<CoinInfoDto> getCoinInfo(String coinName, String coinState,String starDate,String endDate) throws ParseException {
        TimestampConverter timestampConverter = new TimestampConverter();
        return  coinInfoRepository.
                findByStateAndCoinNameAndTimestampBetween(coinState,coinName,timestampConverter.dateToStamp(starDate),timestampConverter.dateToStamp(endDate))
                .stream().map(coinInfo -> CoinInfoDto.builder().
                        coinDate(coinInfo.getCoinDate()).
                   coinName(coinName).
                   closingPrice(coinInfo.getClosingPrice()).
                   maxPrice(coinInfo.getMaxPrice()).
                   minPrice(coinInfo.getMinPrice()).
                   openingPrice(coinInfo.getOpeningPrice()).
                   unitsTraded(coinInfo.getUnitsTraded()).
                        build()).
                collect(Collectors.toList());

    }
}
