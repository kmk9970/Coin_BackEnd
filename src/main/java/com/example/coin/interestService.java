package com.example.coin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class interestService {
    private  final InterestRepository interestRepository;
    public  void save(interest i){interestRepository.save(i);}
    public List<interest> getUserInterest(String id) {
            return interestRepository.getUserInterest(id);
    }
    public List<interest> checkDuple(String userName,String coinName){
        return interestRepository.checkDuple(userName,coinName);
    }
}
