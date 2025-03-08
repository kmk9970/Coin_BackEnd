package com.example.coin.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column
    private String userId;

    @Column
    private String password;

    @Column
    private String nickName;

    @Column
    private String apiKey;

    @Column
    private String secKey;



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterestCoin> interestCoins = new ArrayList<>();

    public void addInterestCoin(InterestCoin coin) {
        interestCoins.add(coin);
        coin.setUser(this);
    }

    public void removeInterestCoin(InterestCoin coin) {
        interestCoins.remove(coin);
        coin.setUser(null);
    }

}
