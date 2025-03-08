package com.example.coin.service;


import com.example.coin.WebCrawler;
import com.example.coin.entity.Kosdaq;
import com.example.coin.entity.StockCode;
import com.example.coin.repository.StockDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional
public class SaveStockDataService {
    private final StockDataRepository saveStockDataRepository;

    public void insertStockCode(){
        String filePath = "/Users/joseungbin/Downloads/coin 21-04-19-055/src/main/java/com/example/coin/csv/stock.csv"; // 파일 경로

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), Charset.forName("EUC-KR")))) {
            String line;
            int cnt = 0;
            br.readLine();
            while ((line = br.readLine()) != null) {
                // CSV 파일의 각 줄을 ,(콤마)로 분리
                String[] values = line.split(",");
                StockCode stockCode = StockCode.builder().
                        code(values[0].substring(1,values[0].length()-1)).
                        name(values[1].substring(1,values[1].length()-1)).
                        state(values[2].substring(1,values[2].length()-1)).
                        stockNum(values[13].substring(1,values[13].length()-1)).
                        build();
                saveStockDataRepository.insertStockCode(stockCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void insertStockPrice() throws IOException {
        //List<StockCode> stockCodes = saveStockDataRepository.getStockCode();

        String url =   "https://finance.naver.com/item/frgn.naver?code=089030&page=";
        WebCrawler webCrawler = new WebCrawler();
        List<List<String>> stockPriceList = webCrawler.getStockPriceData(url);
        for (List<String> stockPrice:stockPriceList){
            Kosdaq kosdaq = Kosdaq.builder().
                    code("089030").
                    coinDate(stockPrice.get(0)).
                    price(stockPrice.get(1)).
                    volume(stockPrice.get(2)).
                    build();
            saveStockDataRepository.insertKosdaq(kosdaq);
        }
    }
}
