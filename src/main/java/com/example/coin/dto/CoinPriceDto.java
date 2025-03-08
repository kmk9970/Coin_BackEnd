package com.example.coin.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoinPriceDto {
    @Column
    private String currentPrice; //현재가격

    @Column
    private String state; //분봉,일봉,월봉

    @Column
    private String coinName; // 코인 이름

    @Column
    private Double rsi; // 코인 rsi 값
}
