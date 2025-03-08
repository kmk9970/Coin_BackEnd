package com.example.coin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampConverter {
    //타임스탬프를 날짜로 변환 해주는 함수
   public  String stampToDate(String timestamp) {
        // 타임스탬프 (밀리초 단위)
        //long timestamp = 1724312880000L;

        // 날짜 형식 지정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");

        // 타임스탬프를 Date 객체로 변환
        Date date = new Date(Long.parseLong(timestamp));

        // 포맷팅된 날짜 출력
        String formattedDate = dateFormat.format(date);
        //System.out.println("Formatted Date: " + formattedDate);
        return formattedDate;
    }
    //날짜를 타임스탬프로 변환 해주는 함수
    public  String dateToStamp(String dateStr) throws ParseException {
        // 날짜 형식 지정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
            // 문자열을 Date 객체로 변환
            Date date = dateFormat.parse(dateStr);
            // Date 객체를 타임스탬프(밀리초 단위)로 변환
            long timestamp = date.getTime();
            // 타임스탬프를 문자열로 변환 후 반환
            return String.valueOf(timestamp);

    }


}
