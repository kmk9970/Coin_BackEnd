package com.example.coin.repository;
import com.example.coin.entity.CoinInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CoinInfoRepository extends JpaRepository<CoinInfo, Long> {

    // state와 coinName으로 검색
    List<CoinInfo> findByStateAndCoinName(String state, String coinName);
    // state, coinName, 그리고 타임스탬프 범위로 검색
    @Query("SELECT c FROM CoinInfo c WHERE c.state = :state AND c.coinName = :coinName AND c.coinDate BETWEEN :startDate AND :endDate")
    List<CoinInfo> findByStateAndCoinNameAndTimestampBetween(
            @Param("state") String state,
            @Param("coinName") String coinName,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    //코인 이름과 상태로 검색후 page table 만큼 최신값에서 가져오는 로직

    @Query("SELECT CAST(c.closingPrice AS double) FROM CoinInfo c WHERE c.coinName = :coinName AND c.state = :state ORDER BY c.coinDate DESC")
    List<Double> findClosingPricesByCoinNameAndState(
            @Param("coinName") String coinName,
            @Param("state") String state,
            Pageable pageable);

    @Query(value = "select exists (select 1 from coin_info where  coin_date  =:coin_date and coin_name =:coin_name)" ,nativeQuery = true)
    int checkDuplicated(@Param("coin_date")String coin_date,
                        @Param("coin_name")String coin_name
                        );
    //인덱스 만드는 명령어
    //CREATE INDEX idx_coin_info_coinname_state_coindate ON coin_info(coin_name, state, coin_date DESC);

    //List<coin_info> findByStateAndCoinNameAndTimestampBetween(String state, String coinName, String startTimestamp, String endTimestamp);
}
