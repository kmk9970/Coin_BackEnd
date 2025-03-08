package com.example.coin.repository;

import com.example.coin.entity.CoinName;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Transactional
@Repository
public interface CoinNameRepository extends JpaRepository<CoinName, Long> {
    // 모든 데이터를 가져오는 메서드 (기본 제공 메서드)
    List<CoinName> findAll();
    Optional<CoinName> findByCoinName(String coinName);
    void deleteByCoinName(String coinName);
}
