package com.example.coin.controller;

import com.example.coin.service.CoinNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CoinNewsController {
    private final CoinNewsService newsService;

    @GetMapping(value = "/coin-news")
    public ResponseEntity<?> getCoinNews(@RequestParam("coin_name")String coin_name) throws IOException, InterruptedException {
        return new ResponseEntity<>(newsService.getCoinNews(coin_name), HttpStatus.OK);
    }
    
}
