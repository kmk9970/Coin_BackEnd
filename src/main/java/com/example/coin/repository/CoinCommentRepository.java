package com.example.coin.repository;

import com.example.coin.entity.CoinComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface CoinCommentRepository extends JpaRepository<CoinComment,Long> {

    @Query("SELECT c FROM CoinComment c WHERE c.coinName = :coinName")
    List<CoinComment> findAllByCoinName(@Param("coinName") String coinName);
}
