package com.example.coin.dto;

import com.example.coin.entity.CoinComment;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CoinCommentDto {

    @Column
    private String coinName;

    @Column
    private String comment;

    @Column
    private String nickName;

    @Column
    private LocalDateTime createdAt;

    public CoinCommentDto(String coinName, String comment, LocalDateTime createdAt, String nickName) {
        this.coinName = coinName;
        this.comment = comment;
        this.createdAt = createdAt;
        this.nickName = nickName;
    }

    public CoinCommentDto(CoinComment comment) {
        this.coinName = comment.getCoinName();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.nickName = comment.getUser().getNickName();
    }
}
