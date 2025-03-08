package com.example.coin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebCrawler {
    public List<List<String>> getStockPriceData (String url) throws IOException {
        int cnt = 1;
        int flag =0;
        List<List<String>> stockPriceList = new ArrayList<>();
        Set<String> visit  = new HashSet<>();
        // URL 설정
        while (true){


            // Jsoup을 이용해 HTML 코드를 가져옴
            Document document = Jsoup.connect(url+String.valueOf(cnt)).get();

            // <table> 태그 추출 (summary 속성을 기준으로 선택)
            Element table = document.select("table[summary='외국인 기관 순매매 거래량에 관한표이며 날짜별로 정보를 제공합니다.']").first();

            // 테이블 내에서 <tr> 태그들을 추출
            Elements rows = table.select("tr");

            // 각 행의 데이터를 출력
            for (Element row : rows) {

                Elements columns = row.select("td");
                //System.out.print(columns.size());
                if (columns.size() != 9) {
                    continue;
                }
                if (visit.contains(columns.get(0).text())){
                    flag = 1;
                    break;
                }
                visit.add(columns.get(0).text());
                List<String> stockPriceData = new ArrayList<>();
                stockPriceData.add(columns.get(0).text()); //날짜
                stockPriceData.add(columns.get(1).text()); //종가
                stockPriceData.add(columns.get(4).text()); //거래량
                stockPriceList.add(stockPriceData);
            }
            if(flag == 1) {
                break;
            }
            cnt++;
        }
        return  stockPriceList;
    }
    public static void main(String[] args) throws IOException {



    }
}
