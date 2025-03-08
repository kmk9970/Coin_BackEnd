package com.example.coin;


import com.example.coin.entity.CoinName;
import com.example.coin.entity.CoinPrice;
import com.example.coin.entity.CoinInfo;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionHistoryRepository {
     private final EntityManager em;
     public void delCoinName(){
         em.createNativeQuery("delete from coin_Name").executeUpdate();;
     }
     public void delCoinInfo(){
         em.createNativeQuery("delete from coin_info").executeUpdate();
     }
     public List<user_info> loginCheck (String identity, String pass){
         return em.createQuery("select m from user_info m where  m.identity =: identity and m.pass =: pass", user_info.class).
                 setParameter("identity",identity).setParameter("pass",pass).setHint("org.hibernate.readOnly",true).getResultList();
     }
     public void excelDataSave(TransactionHistory t){
         em.persist(t);
     }
     public void registMember(user_info u) {
            em.persist(u);
     } //회원가입
    public  List<CoinName> getAllCoinName (){return em.createQuery("select m from coin_Name m",CoinName.class).getResultList();}
     public List<user_info> getAllUserData() {
         return em.createQuery("select m from user_info m",user_info.class).getResultList();
     }
     public List<user_info> getUserData(String id){  //사용자의 정보를 가져오는 함수
         return em.createQuery("select m from user_info m where m.identity =: identity",user_info.class)
                 .setParameter("identity",id)
                 .getResultList();

     }

     public int getCoinSize(){
      Long coinSize=  ((Number) em.createNativeQuery("select count(*) from coin_Name ").getSingleResult()).longValue();;

      return Integer.valueOf(Math.toIntExact(coinSize));
     }
     public  void save(CoinName t){
        em.persist(t);
     }
     public void coinStateSave(CoinInfo c){
         em.persist(c);
     }
     public void dumSave(CoinPrice c) {
         em.persist(c);
     }

     public void saveCoinPrice(List<CoinPrice> c) { //상장일로부터 현재까지 코인 가격
         //배치 insert로 한번에 insert하는 코드인데 먼가 잘안됨..
         long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
         for(int i = 0;i<c.size();i++){
             em.persist(c.get(i));
         }
         em.flush();
         em.clear();
         long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
         long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
         System.out.println("시간차이(m) : "+secDiffTime);
     }
     public void bulkCoinPrice(List<CoinPrice> c){
         long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
         String sql = "INSERT INTO coin_price (id,coin_name,coin_date,opening_price,closing_price,max_price,min_price,unites_traded)values";
         List<CoinPrice> a =  em.createQuery("select m from coin_price m ", CoinPrice.class).getResultList();
         int idNUm = 0;
         if(a.size() !=0){
             idNUm = Integer.valueOf(Math.toIntExact(a.get(a.size() - 1).getId()));
         }
         for(int i=0;i<c.size();i++){
             sql+="(";
             sql+=String.valueOf(idNUm+i+1)+",'";
             sql+=c.get(i).getCoinName()+"','";
             sql+=c.get(i).getCoinDate()+"','";
             sql+=c.get(i).getOpeningPrice()+"','";
             sql+=c.get(i).getClosingPrice()+"','";
             sql+=c.get(i).getMaxPrice()+"','";
             sql+=c.get(i).getMinPrice()+"','";
             sql+=c.get(i).getUnitsTraded();
             sql+="'),";
         }
         sql = sql.substring(0,sql.length()-1);
         em.createNativeQuery(sql).executeUpdate();
         long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
         long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산

         System.out.println("시간차이(m) : "+secDiffTime);
     }
     public  List<CoinInfo> getCoinState(){
         return em.createQuery("select m from coin_info m ", CoinInfo.class).getResultList();
     }
     public List<CoinPrice> getCoinPrice(String coinName){
         return em.createQuery("select m from coin_price m  where m.coinName =: coinName", CoinPrice.class).
                 setParameter("coinName",coinName).getResultList();
     }
    public List<TransactionHistory> getUserTrade(String user){
         return em.createQuery("select m from TransactionHistory m where m.user =: user order by m.coinDate").
                 setParameter("user",user).getResultList();
    }
    public List<String> coinStatistics() { // 사용자들이 매수한 코인들중에서 가장 많이 매매한 순으로 정렬해서 보여주는 함수
        List<Object []>ans= em.createQuery("SELECT coinName, count(state) FROM TransactionHistory c WHERE c.state = ?1 OR c.state = ?2 GROUP BY coinName order by count(state) desc", Object[].class)
                        .setParameter(1, "매도")
                        .setParameter(2, "자동매도")
                        .getResultList();
        List<String> stringList = new ArrayList<>();
        for (Object[] result : ans) {
            // Assuming the coinName is a String, you may need to cast it to String if necessary
            String coinName = (String) result[0];
            stringList.add(coinName);
            stringList.add(String.valueOf ((Long) result[1]));
        }
        return stringList;
    }
    public List<TransactionHistory> getDatafromExcel(String test){
        return new ArrayList<>(em.createQuery("SELECT e FROM TransactionHistory e WHERE e.state = :test", TransactionHistory.class)
                .setParameter("test", test)
                .getResultList());
    }
    public void registUserRanking(user_rank uR){
         em.persist(uR);
    }
    public List<user_rank> getUserRank(String userId){
        return em.createQuery("select m from user_rank m where m.user_id =: user_id ").
                setParameter("user_id",userId).getResultList();
    }

    public List<user_rank> getRank (){
        return em.createQuery("select m from user_rank m order by win_rate desc")
               .getResultList();
    }
}
