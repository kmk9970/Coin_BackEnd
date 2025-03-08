package com.example.coin.repository;


import com.example.coin.entity.CoinInfo;
import com.example.coin.entity.CoinName;
import com.example.coin.entity.CoinPrice;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SaveCoinStateRepository {
    private final EntityManager em;

    public  void saveCoinName(CoinName coinName){
        em.persist(coinName);
    }
    public List<String> getAllCoinName(){
        return  em.createQuery("select m.coinName from CoinName m",String.class).getResultList();
    }
    public CoinName getCoinNameEntity(String coinName) {
        return em.createQuery("select m.coinName from CoinName m where m.coinName =:coinName",CoinName.class).
                setParameter("coinName",coinName).
                getSingleResult();
    }

    @Transactional(readOnly = true)
    public  List<CoinInfo> getCoinInfo( ){
        return em.createQuery("select m from coin_info m ", CoinInfo.class).getResultList();
    }
    @Transactional
    public void saveCoinPrice(CoinPrice coinPrice){
        em.persist(coinPrice);
    }

    @Transactional
    public void saveCoinInfo(String sql){
        try {
            em.createNativeQuery(sql).executeUpdate();
        }catch (Exception e) {
            System.out.println("벌크 인서트 중 에러: ");
            //System.out.println("벌크 인서트 쿼리 : "+sql);
        }
    }
    @Transactional(readOnly = true)
    public Long findMaxId() {
        String jpql = "SELECT MAX(c.id) FROM coin_info c";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

}
