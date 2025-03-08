package com.example.coin.controller;


import com.example.coin.bithumb.UserAsset;
import com.example.coin.dto.UserAssetDto;
import com.example.coin.getUserCash;
import com.example.coin.service.UserAssetService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserAssetController {
    private final UserAssetService userAssetService;
    @GetMapping(value = "/get-user-cash")
    public ResponseEntity<?> getUSerCash () throws JSONException, IOException {
        return new ResponseEntity<>(userAssetService.getUserAsset(), HttpStatus.OK);
    }
}
