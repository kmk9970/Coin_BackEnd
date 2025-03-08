package com.example.coin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(
        name = "coin_info",
        indexes = {
                @Index(name = "idx_coinname_state_coindate", columnList = "coinName, state, coinDate DESC")
        }
)
@NoArgsConstructor
@Entity
public class CoinInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")  //DB칼럼이름이 what //notnull
    private Long id;

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
    private String state; //분봉 , 일봉 , 주봉 , 월봉 상태를 저장

    @Builder
    public CoinInfo(String coinName, String coinDate, String openingPrice,
                    String closingPrice, String maxPrice, String minPrice, String unitsTraded, String state){
        this.coinName = coinName;
        this.coinDate = coinDate;
        this.openingPrice = openingPrice;
        this.closingPrice = closingPrice;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.unitsTraded = unitsTraded;
        this.state = state;
    }
}
