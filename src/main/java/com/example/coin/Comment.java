package com.example.coin;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long comment_id;

    private String text;

    private String writer;

    @ManyToOne(fetch = FetchType.LAZY) //this is content_id
    private Content content;

    public void update(String new_text){
        this.text=new_text;
    }
}