package com.example.coin.controller;


import com.example.coin.service.SaveCoinStateService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SaveCoinStateController {
    private  final SaveCoinStateService saveCoinStateService;


    @GetMapping(value = "/save-coin-name")
    public void saveCoinName() throws JSONException, IOException {
        saveCoinStateService.saveCoinName();
    }

    @GetMapping(value = "/save-coin-price")
    public void saveCoinPrice() throws JSONException, IOException, InterruptedException {
        saveCoinStateService.saveCoinPrice();
    }

    @GetMapping(value = "findMaxId")
    public  void findMaxId(){
        saveCoinStateService.findMaxId();
    }
}
