package com.example.coin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TradeCoinController {
    @PostMapping (value = "/buy-coin")
    public ResponseEntity<?> buyCoin(
            HttpServletRequest request,
            @RequestParam("coinName")String coinName,
            @RequestParam("coinPrice")String coinPrice,
            @RequestParam("amount") String amount){// 매수 할 금액(krw)
            HttpSession session = request.getSession();
            String userId = String.valueOf(session.getAttribute("ID"));

        return new ResponseEntity<>("buy success",HttpStatus.OK);
    }


    @PostMapping (value = "/sell-coin")
    public ResponseEntity<?> sellCoin(HttpServletRequest request ,
                                      @RequestParam("coinName")String coinName,
                                      @RequestParam("amount") String amount){//매도 할 금액(krw)

        return new ResponseEntity<>("sell success",HttpStatus.OK);
    }
}
