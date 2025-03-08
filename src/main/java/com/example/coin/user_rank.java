package com.example.coin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class user_rank {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String user_id;  //유저 id

    @Column
    private String win_rate; //유저 승률

    @Column
    private String profit_rate; //유저 수익률

    @Column
    private String user_amount; //유저의 총자산(코인 + 현금)

}
