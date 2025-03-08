package com.example.coin.repository;


import com.example.coin.entity.CoinInfo;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CoinPriceRepository {
    private final EntityManager em;
    public List<CoinInfo> getCoinPrice(){
        return  em.createQuery("select m from coin_info m where m.state =:state", CoinInfo.class).
        setParameter("state","24h").getResultList();
    }

}
