package com.example.coin.dto;


import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAssetDto {
    @Column //매매 한 코인 이름
    private String coinName;

    @Column //체결 수량
    private String coinAmount;

    @Column //매수 가격
    private String tradePrice;

    @Column //보유 현금 수량
    private String cash;
}
