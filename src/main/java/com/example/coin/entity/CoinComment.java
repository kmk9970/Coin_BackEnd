package com.example.coin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CoinComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coinName")
    private String coinName;

    @Column(name="comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id") // 외래 키 설정
    private User user;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Builder
    public CoinComment(String coinName, String comment, User user){
        this.coinName=coinName;
        this.comment=comment;
        this.user = user;
        this.createdAt=LocalDateTime.now();
    }
}
