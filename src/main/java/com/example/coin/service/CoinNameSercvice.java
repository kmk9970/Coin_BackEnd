package com.example.coin.service;


import com.example.coin.dto.CoinNameDto;
import com.example.coin.entity.CoinName;
import com.example.coin.repository.CoinNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CoinNameSercvice {
    private  final CoinNameRepository coinNameRepository;
    public List<CoinNameDto> getAllCoinName(){
        return coinNameRepository.findAll().stream()
                .map(coinName -> CoinNameDto.builder()
                        .coinCode(coinName.getCoinName())
                        .englishName(coinName.getEnglishName())
                        .koreanName(coinName.getKoreanName())
                        .build())
                .collect(Collectors.toList());
    }
}
