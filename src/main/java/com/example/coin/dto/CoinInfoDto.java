package com.example.coin.dto;


import jakarta.persistence.Column;
import lombok.*;


@Builder
@Getter
public class CoinInfoDto {
    @Column
    private String coinName; //코인 이름

    @Column
    private String coinDate; //기준 시간

    @Column
    private String openingPrice; //시가

    @Column
    private String closingPrice; //종가

    @Column
    private String maxPrice; //고가

    @Column
    private String minPrice; //저가

    @Column
    private String unitsTraded; //거래량

    @Column
    private String rsi;
}
