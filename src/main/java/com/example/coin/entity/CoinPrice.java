package com.example.coin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        indexes = {
                @Index(name = "idx_coin_name", columnList = "coin_name_id")  // 인덱스 설정
        }
)
@Getter
@NoArgsConstructor
@Entity
public class CoinPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne
    @JoinColumn(name = "coin_name_id", nullable = false)  // 외래 키 설정
    private CoinName coinName; // 코인 이름

    @Column(nullable = false)
    private String coinDate; // 기준 시간

    @Column(nullable = false)
    private String openingPrice; // 시가

    @Column(nullable = false)
    private String closingPrice; // 종가

    @Column(nullable = false)
    private String maxPrice; // 고가

    @Column(nullable = false)
    private String minPrice; // 저가

    @Column(nullable = false)
    private String unitsTraded; // 거래량

    @Column(nullable = false)
    private String state; // 분봉, 일봉, 주봉, 월봉 상태 저장

    @Builder
    public CoinPrice(CoinName coinName, String coinDate, String openingPrice,
                     String closingPrice, String maxPrice, String minPrice, String unitsTraded, String state) {
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
