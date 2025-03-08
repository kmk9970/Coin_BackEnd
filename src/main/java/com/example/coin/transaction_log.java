package com.example.coin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class transaction_log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)  //DB칼럼이름이 what //notnull
    private Long id;

    private String coin_name; //체결한 코인 이름

    private double amount; // 보유중인 코인 수

    private String user_id; //유저 이름

    private String coin_price; //체결 당시 코인 가격
}
