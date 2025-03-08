//package com.example.coin;
//
//import org.deeplearning4j.nn.api.OptimizationAlgorithm;
//import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
//import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
//import org.deeplearning4j.nn.conf.layers.LSTM;
//import org.deeplearning4j.nn.conf.layers.OutputLayer;
//import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
//import org.deeplearning4j.nn.weights.WeightInit;
//import org.deeplearning4j.optimize.api.IterationListener;
//import org.nd4j.linalg.activations.Activation;
//import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
//import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
//import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
//import org.nd4j.linalg.lossfunctions.LossFunctions;
//import org.nd4j.linalg.api.ndarray.INDArray;
//import org.nd4j.linalg.factory.Nd4j;
//
//import java.util.*;
//
//public class CoinPatternMatcherDL4J {
//
//    // 데이터 준비
//    public static List<double[]> generateDummyData(int numSamples, int numFeatures) {
//        Random random = new Random();
//        List<double[]> data = new ArrayList<>();
//
//        for (int i = 0; i < numSamples; i++) {
//            double[] row = new double[numFeatures];
//            for (int j = 0; j < numFeatures; j++) {
//                row[j] = random.nextDouble() * 1000; // Random values for open, high, low, close, volume
//            }
//            data.add(row);
//        }
//        return data;
//    }
//
//    // 모델 구성
//    public static MultiLayerConfiguration createLSTMModel(int inputSize, int outputSize) {
//        return new NeuralNetConfiguration.Builder()
//
//                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
//                .weightInit(WeightInit.XAVIER)
//                .updater(new org.nd4j.linalg.learning.config.Adam(0.001))
//                .list()
//                .layer(0, new LSTM.Builder()
//                        .nIn(inputSize)
//                        .nOut(64)
//                        .activation(Activation.TANH)
//                        .build())
//                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
//                        .nIn(64)
//                        .nOut(outputSize)
//                        .activation(Activation.IDENTITY)
//                        .build())
//                .build();
//    }
//
//    // 데이터 전처리
//    public static INDArray preprocessData(List<double[]> data, int timeSteps) {
//        int numSamples = data.size() - timeSteps;
//        int numFeatures = data.get(0).length;
//
//        INDArray input = Nd4j.create(numSamples, timeSteps, numFeatures);
//        for (int i = 0; i < numSamples; i++) {
//            for (int j = 0; j < timeSteps; j++) {
//                try {
//                    input.putRow(i, Nd4j.create(data.get(i + j)));
//                   //Nd4j.create(data.get(i + j));
//                }catch (Exception e){
//                    System.out.println("에러 "+e);
//                }
//            }
//        }
//        return input;
//    }
//
//    // 유사도 계산
//    public static double calculateCosineSimilarity(INDArray vec1, INDArray vec2) {
//        double dotProduct = vec1.mul(vec2).sumNumber().doubleValue();
//        double norm1 = vec1.norm2Number().doubleValue();
//        double norm2 = vec2.norm2Number().doubleValue();
//        return dotProduct / (norm1 * norm2);
//    }
//
//    public static void main(String[] args) {
//        int numSamples = 1000; // 데이터 샘플 수
//        int numFeatures = 5;  // open, high, low, close, volume
//        int timeSteps = 10;   // 시계열 데이터 길이
//
//        // 1. 데이터 생성 및 전처리
//        List<double[]> data = generateDummyData(numSamples, numFeatures);
//        INDArray input = preprocessData(data, timeSteps);
//
//        // 2. 모델 생성
//        MultiLayerConfiguration conf = createLSTMModel(numFeatures, numFeatures);
//        MultiLayerNetwork model = new MultiLayerNetwork(conf);
//        model.init();
//
//        // 3. 학습
//        // 학습 데이터를 구성하고 모델을 학습시키는 코드를 추가합니다.
//
//        // 4. 예측 및 유사도 계산
//        INDArray targetPattern = input.getRow(0); // 예: 첫 번째 패턴을 대상으로 설정
//        INDArray predictions = model.output(input);
//
//        for (int i = 0; i < predictions.rows(); i++) {
//            INDArray candidatePattern = predictions.getRow(i);
//            double similarity = calculateCosineSimilarity(targetPattern, candidatePattern);
//            System.out.println("Similarity with pattern " + i + ": " + similarity);
//        }
//    }
//}
