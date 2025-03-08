package com.example.coin.service;

import com.example.coin.repository.CoinPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CoinPriceScheduler {
    private final CoinPriceRepository coinPriceRepository;
//    @Scheduled(fixedRate = 1000) //1초에 한번씩
//    public void fetchAndSaveCoinPrices() {
//
//    }
}
