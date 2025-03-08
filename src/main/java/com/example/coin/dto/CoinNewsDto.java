package com.example.coin.dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CoinNewsDto {
    @Column
    private String title; //기사 제목

    @Column
    private String url; // 하이퍼링크

    @Column
    private String newspaper; //신문사

    @Column
    private String image; //이미지 하이퍼링크

//    @Column //현재로는 ?분전 밖에 안됨 문의 후 결정
//    private String datetime;
}
