package com.example.coin.service;

import com.example.coin.dto.CoinCommentDto;
import com.example.coin.entity.CoinComment;
import com.example.coin.repository.CoinCommentRepository;
import com.example.coin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinCommentService {

    private final CoinCommentRepository commentRepository;
    private final UserRepository userRepository;

    public List<CoinCommentDto> getCoinCommentList(String coinName) {
        List<CoinCommentDto> commentDto = commentRepository.findAllByCoinName(coinName)
                .stream()
                .map(CoinCommentDto::new)
                .toList();
        return commentDto;
    }

    public void saveComment(String coinName, Long userId, String comment) {
        commentRepository.save(CoinComment.builder()
                .user(userRepository.findById(userId).get())
                .comment(comment)
                .coinName(coinName)
                .build());
    }
}
