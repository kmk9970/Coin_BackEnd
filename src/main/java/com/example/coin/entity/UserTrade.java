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
public class UserTrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userId;  // 유저 아이디

    @Column
    private String coinName; // 매매한 코인 이름

    @Column
    private String state;  // 매수*매도 상태

    @Column
    private String tradePrice ; // 체결 가격

}
