package com.example.coin.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CoinName {
    @Id
    @Column(nullable = false, unique = true)
    private String coinName;

    @Column
    private String englishName;

    @Column
    private String koreanName;
}
