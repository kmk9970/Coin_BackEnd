package com.example.coin.controller;


import com.example.coin.dto.CoinNameDto;
import com.example.coin.service.CoinNameSercvice;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CoinNameController {
    private final CoinNameSercvice coinNameSercvice;
    @GetMapping(value = "/coin-name-list")
    public ResponseEntity<List<CoinNameDto>> getCoinNameList(){

        return new ResponseEntity<>(coinNameSercvice.getAllCoinName(), HttpStatus.OK);
    }
}
