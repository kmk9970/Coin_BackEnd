package com.example.coin;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InterestRepository {
    private final EntityManager EM;
    public void save(interest ii) {

        EM.persist(ii);
    }
    public  List<interest> getUserInterest(String userName){
        return EM.createQuery("select  m from interest m where m.userName =: userName",interest.class)
                .setParameter("userName",userName)
                .getResultList();
    }
    public List<interest> checkDuple(String userName,String coinName){
        return EM.createQuery("select  m from interest m where m.userName =: userName and m.coinName =: " +
                        "coinName",interest.class)
                .setParameter("userName",userName).setParameter("coinName",coinName)
                .getResultList();
    }
    public void edit(interest i) {
        EM.merge(i);
    }

    public void delete(int id) {
//        contents.remove(id);
        interest i = EM.find(interest.class,id);
        if(i!=null){
            EM.remove(i);
        }
    }

    public List<interest> findAll() {
//        return new ArrayList<>(contents.values());
//        return EM.createQuery("SELECT c FROM Content c", Content.class).getResultList();
        return new ArrayList<>(EM.createQuery("SELECT c FROM Interest c", interest.class).getResultList());
    }

    public interest findById(int id) {
//        return contents.get(id);
        return EM.find(interest.class,id);
    }

    public List<interest> findByCoinName(String coinName) {
        return EM.createQuery("SELECT c FROM Interest c WHERE c.coinName = :coinName", interest.class)
                .setParameter("coinName", coinName)
                .getResultList();
    }
}
