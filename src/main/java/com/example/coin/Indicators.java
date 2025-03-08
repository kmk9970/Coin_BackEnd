package com.example.coin;


import java.util.ArrayList;
import java.util.List;

//보조지표 관련 함수를 가지는 클래스
public class Indicators {

    private List<Double> closingPrices;   // 종가 목록

    // 생성자: 종가 리스트를 받아서 저장
    public Indicators(List<Double> closingPrices) {
        this.closingPrices = new ArrayList<>(closingPrices);
    }
    public  double calculateRSI( int period) {
        if (closingPrices.size() < period) {
            throw new IllegalArgumentException("종가 데이터가 충분하지 않습니다. (" + period + "개 필요)");
        }

        List<Double> delta = new ArrayList<>();
        List<Double> ups = new ArrayList<>();
        List<Double> downs = new ArrayList<>();

        // 1. 가격 변화 계산
        for (int i = 1; i < closingPrices.size(); i++) {
            double change = closingPrices.get(i) - closingPrices.get(i - 1);
            delta.add(change);
            ups.add(Math.max(change, 0));
            downs.add(Math.max(-change, 0));
        }

        // 2. EMA 계산
        double au = calculateLatestEMA(ups, period);
        double ad = calculateLatestEMA(downs, period);

        // 3. RSI 계산
        if (ad == 0) {
            return 100.0; // 손실이 없으면 RSI는 100
        }

        double rs = au / ad;
        return 100 - (100 / (1 + rs));
    }

    private static double calculateLatestEMA(List<Double> values, int period) {
        if (values.size() < period) {
            throw new IllegalArgumentException("데이터가 충분하지 않습니다. (" + period + "개 필요)");
        }

        double multiplier = 2.0 / (period + 1);
        double ema = 0.0;

        // 초기 EMA는 SMA로 계산
        for (int i = 0; i < period; i++) {
            ema += values.get(i);
        }
        ema /= period;

        // 이후 EMA 계산
        for (int i = period; i < values.size(); i++) {
            ema = ((values.get(i) - ema) * multiplier) + ema;
        }

        return ema;
    }
//    public double calculateRSI(int period) {
//        if (closingPrices.size() < period) {
//            throw new IllegalArgumentException("종가 데이터가 충분하지 않습니다. (" + period + "개 필요)");
//        }
//
//        double gainSum = 0.0;
//        double lossSum = 0.0;
//
//        for (int i = closingPrices.size() - period; i < closingPrices.size() - 1; i++) {
//            double change = closingPrices.get(i + 1) - closingPrices.get(i);
//            if (change > 0) {
//                gainSum += change;
//            } else {
//                lossSum += Math.abs(change);
//            }
//        }
//
//        double avgGain = gainSum / period;
//        double avgLoss = lossSum / period;
//
//        if (avgLoss == 0) {
//            return 100.0; // 손실이 없으면 RS가 무한대 -> RSI=100(초강세)
//        }
//
//        double rs = avgGain / avgLoss;
//        double rsi = 100.0 - (100.0 / (1.0 + rs));
//        return rsi;
//    }


    /**
     * Simple Moving Average (단순 이동평균)
     */
    public List<Double> calculateSMA(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException("기간(period)은 1 이상이어야 합니다.");
        }
        if (period > closingPrices.size()) {
            throw new IllegalArgumentException("기간(period)이 데이터 크기보다 큽니다.");
        }

        List<Double> smaValues = new ArrayList<>();
        double sum = 0.0;

        for (int i = 0; i < closingPrices.size(); i++) {
            sum += closingPrices.get(i);

            // period 이전까지는 누적
            if (i >= period) {
                sum -= closingPrices.get(i - period);
            }

            // period-1 인덱스부터가 실제 SMA 시작점
            if (i >= period - 1) {
                double sma = sum / period;
                smaValues.add(sma);
            }
        }

        return smaValues;
    }


    /**
     * Bollinger Bands 최종 값 하나만 반환
     *  - 여기서는 중심선(middleBand) 리스트의 "마지막 값"만 반환
     *  - 실제로는 upperBand, lowerBand, 밴드폭(upper-lower) 등 원하는 값을 반환할 수도 있음
     */
    //lowerBand를 리턴하는 함수
    public double calculateBollingerBands(int period, double k) {
        List<Double> smaList = calculateSMA(period);
        List<Double> upperBand = new ArrayList<>();
        List<Double> lowerBand = new ArrayList<>();

        for (int i = 0; i < smaList.size(); i++) {
            int actualIndex = i + (period - 1);

            double sum = 0.0;
            for (int j = actualIndex - period + 1; j <= actualIndex; j++) {
                sum += closingPrices.get(j);
            }
            double mean = smaList.get(i);

            // 표준편차 계산
            double variance = 0.0;
            for (int j = actualIndex - period + 1; j <= actualIndex; j++) {
                double diff = closingPrices.get(j) - mean;
                variance += diff * diff;
            }
            double stdev = Math.sqrt(variance / period);

            // upper, lower 계산
            upperBand.add(mean + k * stdev);
            lowerBand.add(mean - k * stdev);
        }

        // 3개 리스트 중 원하는 값(여기서는 middleBand의 가장 최신 값)만 반환
        if (smaList.isEmpty()) {
            // 예외 상황 처리
            return 0.0;
        }
        //  return smaList.get(smaList.size() - 1);  // 마지막 middleBand를 반환
        return lowerBand.get(lowerBand.size() - 1);  // 마지막 lowerBand를 반환

    }
}

