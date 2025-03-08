package com.example.coin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class trade_history {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)  //DB칼럼이름이 what //notnull
    private Long id;


}
