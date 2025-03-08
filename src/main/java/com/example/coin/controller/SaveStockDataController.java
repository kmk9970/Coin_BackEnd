package com.example.coin.controller;


import com.example.coin.service.SaveStockDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SaveStockDataController {
    private final SaveStockDataService saveStockDataService;
    @GetMapping(value = "/insert-stock-code")
    public void insertStockCode(){
        saveStockDataService.insertStockCode();
    }
    @GetMapping(value = "/insert-stock-price")
    public void insertStockPrice() throws IOException {
        saveStockDataService.insertStockPrice();
    }
}
