package com.example.coin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table
@Entity
@NoArgsConstructor
@Getter
public class StockCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;


    @Column(unique = true)
    private String code; //종목코드

    @Column
    private String name; //종목명

    @Column
    private String  state;  //코스닥,코스피

    @Column
    private String stockNum; //상장 주식수

    @Builder
    public StockCode(String code,String name,String state,String stockNum){
        this.code = code;
        this.name = name;
        this.state = state;
        this.stockNum = stockNum;
    }

}
