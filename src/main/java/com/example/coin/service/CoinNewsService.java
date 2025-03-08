package com.example.coin.service;

import com.example.coin.dto.CoinNewsDto;
import com.example.coin.scrapper.CoinInfoScrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoinNewsService {

    private final CoinInfoScrapper scrapper = new CoinInfoScrapper();

    public List<CoinNewsDto> getCoinNews(String coin_name) throws IOException, InterruptedException {
        return scrapper.getCoinNews(coin_name);
    }
}
