package com.example.coin.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
@Getter
public class Kosdaq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String code; //종목코드

    @Column
    private String coinDate;

    @Column
    private String price; //종가

    @Column
    private String volume;

    @Builder
    public Kosdaq(String code,String coinDate,String price,String volume){
        this.code = code;
        this.coinDate = coinDate;
        this.price = price;
        this.volume = volume;
    }
}
