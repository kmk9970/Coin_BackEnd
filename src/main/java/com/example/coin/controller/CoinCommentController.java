package com.example.coin.controller;

import com.example.coin.dto.CoinCommentDto;
import com.example.coin.service.CoinCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CoinCommentController {
    //1.코인에 대한 모든 댓글 조회 ok
    //2.코인에 대한 댓글 작성 ok
    //아래 두개 생각하기 전에 dto에 대해서 더 고민 ㄱ
    //3.코인에 대한 댓글 삭제
    //4.코인에 대한 댓글 수정

    private final CoinCommentService commentService;

    @GetMapping(value = "/comment-list")
    public ResponseEntity<List<CoinCommentDto>> getCoinCommentList(@RequestParam("coinName")String coinName){
        return ResponseEntity.ok()
                .body(commentService.getCoinCommentList(coinName));
    }

    @PostMapping(value = "/save-comment")
    public void saveCoinComment( //일단 리스폰스 엔티티로 받을지는 고민
            @RequestParam("coinName")String coinName,
            @RequestParam("user_id")Long user_id,
            @RequestParam("comment")String comment){
        commentService.saveComment(coinName,user_id,comment);
    }

//    @DeleteMapping(value = "/delete-comment")
//    public void deleteCoinComment(
//            @RequestParam("coinName")String coinName,
//            @RequestParam("")
//    )



}
