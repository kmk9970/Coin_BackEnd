package com.example.coin.dto;


import jakarta.persistence.Column;
import lombok.*;

@Getter

@Builder
public class CoinNameDto {
    @Column
    private String coinCode ;

    @Column
    private String englishName ;

    @Column
    private String koreanName;
}
