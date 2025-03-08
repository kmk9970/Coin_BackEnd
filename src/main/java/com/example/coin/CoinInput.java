package com.example.coin;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoinInput {
    @NotEmpty(message ="코인이름")
    private String coinName;
}
