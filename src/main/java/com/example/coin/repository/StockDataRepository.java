package com.example.coin.repository;


import com.example.coin.entity.Kosdaq;
import com.example.coin.entity.StockCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StockDataRepository {
    private final EntityManager em;

    public void insertStockCode(StockCode stockCode){
        em.persist(stockCode);
    }
    public List<StockCode> getStockCode(){
      return   em.createQuery("select m from StockCode m",StockCode.class).getResultList();
    }

    public void insertKosdaq(Kosdaq kosdaq){
        em.persist(kosdaq);
    }
}
