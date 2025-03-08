package com.example.coin;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CoinPatternMatcher {

    // 변화율 계산 (여러 인자)
    public List<List<Double>> calculateChangeRate(List<Map<String, Double>> data) {
        List<List<Double>> changeRates = new ArrayList<>();
        for (int i = 0; i < data.size() - 1; i++) {
            List<Double> rate = new ArrayList<>();
            for (String key : List.of("open", "high", "low", "close", "volume")) {
                rate.add(data.get(i + 1).get(key) / data.get(i).get(key));
            }
            changeRates.add(rate);
        }
        return changeRates;
    }

    // DTW 유사도 계산
    public double calculateDTWDistance(List<List<Double>> targetPattern, List<List<Double>> candidatePattern) {
        int n = targetPattern.size();
        int m = candidatePattern.size();
        double[][] dtw = new double[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            Arrays.fill(dtw[i], Double.MAX_VALUE);
        }
        dtw[0][0] = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                double cost = 0.0;
                for (int k = 0; k < targetPattern.get(i - 1).size(); k++) {
                    cost += Math.abs(targetPattern.get(i - 1).get(k) - candidatePattern.get(j - 1).get(k));
                }
                dtw[i][j] = cost + Math.min(dtw[i - 1][j], Math.min(dtw[i][j - 1], dtw[i - 1][j - 1]));
            }
        }

        return dtw[n][m];
    }

    // 데이터 필터링
    public List<Map<String, Object>> filterData(List<Map<String, Object>> data, String startDate, String endDate, String coinName) {
        return data.stream()
                .filter(entry -> entry.get("coin").equals(coinName) &&
                        ((String) entry.get("date")).compareTo(startDate) >= 0 &&
                        ((String) entry.get("date")).compareTo(endDate) <= 0)
                .collect(Collectors.toList());
    }

    // 메인 로직
    public void findSimilarPatterns(List<Map<String, Object>> data, String startDate, String endDate, String coinName) {
        // 사용자 요청 데이터 필터링
        List<Map<String, Object>> targetData = filterData(data, startDate, endDate, coinName);
        List<Map<String, Double>> targetValues = targetData.stream()
                .map(entry -> Map.of(
                        "open", (Double) entry.get("open"),
                        "high", (Double) entry.get("high"),
                        "low", (Double) entry.get("low"),
                        "close", (Double) entry.get("close"),
                        "volume", (Double) entry.get("volume")
                ))
                .collect(Collectors.toList());
        List<List<Double>> targetPattern = calculateChangeRate(targetValues);

        // 모든 코인 데이터와 비교
        List<Map<String, Object>> results = new ArrayList<>();
        Set<String> coins = data.stream().map(entry -> (String) entry.get("coin")).collect(Collectors.toSet());

        for (String coin : coins) {
            List<Map<String, Object>> coinData = data.stream()
                    .filter(entry -> entry.get("coin").equals(coin))
                    .collect(Collectors.toList());

            for (int i = 0; i <= coinData.size() - targetValues.size(); i++) {
                List<Map<String, Double>> candidateValues = coinData.subList(i, i + targetValues.size()).stream()
                        .map(entry -> Map.of(
                                "open", (Double) entry.get("open"),
                                "high", (Double) entry.get("high"),
                                "low", (Double) entry.get("low"),
                                "close", (Double) entry.get("close"),
                                "volume", (Double) entry.get("volume")
                        ))
                        .collect(Collectors.toList());
                List<List<Double>> candidatePattern = calculateChangeRate(candidateValues);

                // DTW 유사도 계산
                double dtwDistance = calculateDTWDistance(targetPattern, candidatePattern);

                // 결과 저장
                Map<String, Object> result = new HashMap<>();
                result.put("coin", coin);
                result.put("startDate", coinData.get(i).get("date"));
                result.put("endDate", coinData.get(i + targetValues.size() - 1).get("date"));
                result.put("distance", dtwDistance);
                results.add(result);
            }
        }

        // 결과 정렬 및 출력 (유사도가 낮은 순으로)
        results.sort(Comparator.comparingDouble(r -> (Double) r.get("distance")));
        results.stream().limit(5).forEach(result -> {
            System.out.printf("Coin: %s, Period: %s ~ %s, Distance: %.4f%n",
                    result.get("coin"),
                    result.get("startDate"),
                    result.get("endDate"),
                    result.get("distance"));
        });
    }

    // 더미 데이터 생성
    public List<Map<String, Object>> generateDummyData() {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();
        String[] coins = {"A", "B", "C", "D", "E"};
        LocalDate startDate = LocalDate.of(2024, 1, 1);

        for (int i = 0; i < 100; i++) {
            String coin = coins[random.nextInt(coins.length)];
            LocalDate date = startDate.plusDays(random.nextInt(365)); // 2024년 내의 랜덤 날짜
            data.add(Map.of(
                    "coin", coin,
                    "date", date.toString(),
                    "open", random.nextDouble() * 1000 + 10,
                    "high", random.nextDouble() * 1000 + 20,
                    "low", random.nextDouble() * 1000 + 5,
                    "close", random.nextDouble() * 1000 + 10,
                    "volume", random.nextDouble() * 10000 + 100
            ));
        }
        return data;
    }

    // 실행 예제
    public static void main(String[] args) {
        CoinPatternMatcher matcher = new CoinPatternMatcher();
        List<Map<String, Object>> data = matcher.generateDummyData();
        matcher.findSimilarPatterns(data, "2024-06-01", "2024-06-03", "A");
    }
}
