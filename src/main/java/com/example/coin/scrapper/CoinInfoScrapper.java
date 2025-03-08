package com.example.coin.scrapper;

import com.example.coin.dto.CoinNewsDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class CoinInfoScrapper { //네이버에서 뉴스 기사를 최신순으로 긁어온다.
    private static final String url = "https://search.naver.com/search.naver?where=news&query=";
    //<li class="bx" id="sp_nws1">에서 sp_nws5까지 읽자(지금은 10개 읽어오고 있음)
    //class="news_tit"인 a 태그에서 링크와 제목을 읽을 수 있다.
    public List<CoinNewsDto> getCoinNews(String coin) throws IOException, InterruptedException {
        List<CoinNewsDto> NewsList = new ArrayList<>();

        Document document = Jsoup.connect(url + coin+"&=tab_opt&sort=1")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36")
                .timeout(10000)
                .get();
        Elements group_news = document.select("div.group_news");
        Elements newsList = group_news.select("li");

        for(Element li : newsList){ //이대로 작동시키면 가장 최근 10개의 뉴스기사를 읽어온다.
            String newspaper = li.selectFirst("div.news_info .info_group > a.info.press").ownText().trim(); //신문사
            Element NewsImage = li.selectFirst("div.news_contents .dsc_thumb > img.thumb");
            String image="None";
            if (NewsImage != null) {
                image = NewsImage.attr("data-lazysrc"); // 이미지 링크
            }
            Element newsContentsDiv = li.select("div.news_contents").first();
            if (newsContentsDiv != null) {
                Element content = newsContentsDiv.select("a").eq(1).first();
                if (content != null) {
                    String link = content.attr("href");  // 링크
                    String title = content.attr("title");  // 텍스트
                    NewsList.add(new CoinNewsDto(title, link, newspaper, image));
                }
            }
        }
        return NewsList;
    }

    public void TestScarap(String coin) throws IOException {
        Document document = Jsoup.connect(url + coin+"&=tab_opt&sort=1")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36")
                .timeout(10000)
                .get();
        System.out.print(document);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        CoinInfoScrapper scrapper = new CoinInfoScrapper();
//        scrapper.TestScarap("비트코인");
        scrapper.getCoinNews("비트코인");
    }

}
