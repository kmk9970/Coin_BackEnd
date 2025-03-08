package com.example.coin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jdk.jfr.Enabled;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class user_asset {
    @Id
    @GeneratedValue//(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)  //DB칼럼이름이 what //notnull
    private Long id;

    private String total_currency;
    private String total_krw;
    private String available_currency;
    private String available_krw;
}
