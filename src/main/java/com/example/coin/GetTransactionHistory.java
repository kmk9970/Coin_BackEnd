package com.example.coin;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GetTransactionHistory {

    static List<String> answer = new ArrayList<>();
    static List<Integer> threadList = new ArrayList<>();
    private  static CountDownLatch countDownLatch;
    private static boolean isLocked = false;
    static Thread lockedBy = null;
    static int lockedCount = 0;

    //private static Lock lock = new ReentrantLock();
    static Lock lock = new Lock() {
        @Override
        public synchronized void lock() {
            Thread callingThread = Thread.currentThread();
            while (isLocked && lockedBy != callingThread) {
                try {
                    wait(); // 대기
                } catch (Exception e) {
                    System.out.println("lock: " + e);
                }
            }

            isLocked = true;
            lockedCount++;
            lockedBy = callingThread;
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            // 인터럽트 가능한 락
        }

        @Override
        public boolean tryLock() {
            // 락 시도
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            // 일정 시간 동안 락 시도
            return false;
        }

        @Override
        public synchronized void unlock() {
            if (Thread.currentThread() == lockedBy) {
                lockedCount--;

                if (lockedCount == 0) {
                    isLocked = false;
                    notify();  // 락 해제
                }
            }
        }

        @Override
        public Condition newCondition() {
            return null;
        }
    };
    private static String key = "";
    private static String sec= "";
    private static  Api_Client api;

    public GetTransactionHistory (String key,String sec,int coinSize){
        this.key = key;
        this.sec = sec;
        this.api = new Api_Client(key,sec);
       this.countDownLatch = new CountDownLatch(coinSize);
    }
    //빗썸 회원정보를 알려주는 코드
    public String getCoinPrice(String coinName) throws IOException, JSONException {
        String URL = "https://api.bithumb.com/public/transaction_history/"+coinName+"_KRW";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        JSONObject json = new JSONObject(body.string());
        JSONArray dataArray= json.getJSONArray("data");

        JSONObject json1 = new JSONObject(dataArray.get(dataArray.length()-1).toString());
        String coinPrice = json1.get("price").toString();
        return coinPrice;
    }
    public static  String getUserCash (){
        String total_krw = "";
        String result="";
        String apiKey = "e8d62d019ec15f02480fff030e52db42";
        String apiSec = "eddf5053c29a30136cc954f41d49df65";
        Api_Client call_api = new Api_Client(apiKey,apiSec);
        try {

            HashMap<String, String> rgParams = new HashMap<String, String>();
            rgParams.put("currency", String.valueOf("BTC"));
            result = call_api.callApi("/info/balance", rgParams);

            JSONObject json = new JSONObject(result);
            String data = json.getString("data"); //string()쓰기

            JSONObject json1 = new JSONObject(data);
            total_krw = json1.getString("total_krw").toString();
        }
        catch (Exception e) {

        }

        return total_krw;
    }
    public static  List<String> getUserAsset(String coinName,int i) throws JSONException, IOException {

        List<String> myCoin = new ArrayList<>();
        String result="";
        String total_krw = "";
        String total_currency ="";
        try {

           HashMap<String, String> rgParams = new HashMap<String, String>();
           rgParams.put("currency", String.valueOf(coinName));
             result = api.callApi("/info/balance", rgParams);
            //System.out.println(result);
           JSONObject json = new JSONObject(result);
           String data = json.getString("data"); //string()쓰기

           JSONObject json1 = new JSONObject(data);
           total_krw = json1.getString("total_krw").toString();
           total_currency = json1.get("total_" + coinName.toLowerCase()).toString();
          // lock.lock();
       }catch (Exception e) {
            System.out.println(coinName+".오류" + e+" "+result);
//            lock.unlock();
//            countDownLatch.countDown();
            return myCoin;
       }finally{

            if (Double.valueOf(total_currency) > 0.00001) {
                //System.out.println("보유한 원화: " + total_krw + "  보유한 " + coinName + ": " + total_currency);
                //String coinPrice = getCoinPrice(coinName);
                answer.add(coinName);
                answer.add(total_currency);
                myCoin.add(total_krw);
                myCoin.add(coinName);
                myCoin.add(total_currency);
            }

        }


      // }
        //countDownLatch.countDown();
        return myCoin;
    }
    public void multiThread (){

    }
    public List<String> executeAnsConcurrently(List<String> coinName) {
        // 스레드 풀을 생성합니다.
        //ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        ExecutorService executor = Executors.newFixedThreadPool(coinName.size());

        //executor.setCorePoolSize(coinName.size()); // 스레드 풀의 크기를 설정합니다. 원하는 크기로 조절할 수 있습니다.
        System.out.println("전체 코인사이즈: "+ coinName.size());
//        executor.setMaxPoolSize(coinName.size()*2);
//        executor.setQueueCapacity(coinName.size()*20);
        List<List<String>> myAsset = new ArrayList<>();
        // 스레드 풀을 초기화합니다.
        //executor.initialize();
        try{
      // for (int i = 0; i < coinName.size(); i++)
            for (int i = 0; i < 140; i++) {
                final String temp = coinName.get(i);
                final int num = i;
            executor.execute(() -> {
                try {
                   getUserAsset(temp,num);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } );// 함수를 별도의 스레드에서 실행합니다.
        }
            Thread.sleep(5000);  //1초 대기 --> 빗썸 api에서 초당 요청할 수 있는 횟수가 제한적이어서
            for(int i= 140;i<coinName.size();i++){
                final String temp = coinName.get(i);
                final int num = i;
                executor.execute(() -> {
                    try {
                        getUserAsset(temp,num);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } );// 함수를 별도의 스레드에서 실행합니다.
            }
        }catch (Exception e){

        }
        try {
            countDownLatch.await(); // Wait for all threads to finish
        } catch (Exception e) {
            System.out.println(e);
        }
//        System.out.println("내 보유코인 " + answer.size());
//        for(int i = 0; i < answer.size();i++){
//            System.out.println(answer.get(i));
//        }

        executor.shutdown();
        return answer;
    }
    public  static Map<String, String> transactionHistory(){
        String key = "e8d62d019ec15f02480fff030e52db42";
        String sec = "eddf5053c29a30136cc954f41d49df65";
        Api_Client api = new Api_Client(key,sec);
        HashMap<String, String> rgParams = new HashMap<String, String>();
        //order_currency=QTUM&payment_currency=KRW
        rgParams.put("order_currency", "XCN");
        rgParams.put("payment_currency", "KRW");
        String amount="";
        String order_currency="";
        String price="";
        try {
            String result = api.callApi("/info/user_transactions", rgParams);

            JSONObject json = new JSONObject(result);
            String data = json.get("data").toString();
            //System.out.println("정답"+);
            JSONObject answer = new JSONObject(data.substring(1,data.length()-1));
            String search = answer.get("search").toString(); //거래 종류구분(매수,매도 ..등등)
            amount = answer.get("amount").toString();//거래금액
            order_currency = answer.get("order_currency").toString();//거래한 코인이름
            price = answer.get("price").toString(); //평단
            Long timestamp = Long.parseLong(answer.get("transfer_date").toString());

            TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
            Date date = new Date(timestamp);
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
            //dateFormat.setTimeZone(timeZone);
            String formattedDate = dateFormat.format(date);


            System.out.println(amount);
            System.out.println(order_currency);
            System.out.println(price);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> info = new HashMap<String, String>();
        info.put("amount",amount);
        info.put("order_currency",order_currency);
        info.put("price",price);
        return info;
    }
    public static void getUserHistory() throws JSONException, IOException {
        String key = "e8d62d019ec15f02480fff030e52db42";
        String sec = "eddf5053c29a30136cc954f41d49df65";
        Api_Client api = new Api_Client(key,sec);
        HashMap<String, String> rgParams = new HashMap<String, String>();
        ///rgParams.put("units", "1000.0000");
        rgParams.put("order_currency", "XCN");
        //rgParams.put("payment_currency","KRW");
        String result = api.callApi("/info/orders", rgParams);
        //System.out.println(result);
        JSONObject json = new JSONObject(result);
        String data = json.get("message").toString();
        System.out.println(data);
    }
    public static Map<String,String> userTradeHistory() throws JSONException, IOException {
        String key = "e8d62d019ec15f02480fff030e52db42";
        String sec = "eddf5053c29a30136cc954f41d49df65";
        Api_Client api = new Api_Client(key,sec);
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("order_currency", "QTUM");
        rgParams.put("order_id", "C0110000000209048652");
        String result = api.callApi("/info/order_detail", rgParams);
        System.out.println(result);
        JSONObject json = new JSONObject(result);
        String data = json.get("data").toString();


        Map<String, String> info = new HashMap<String, String>();
//        info.put("amount",amount);
//        info.put("order_currency",order_currency);
//        info.put("price",price);
        return info;
    }
    public static void buy_coin() throws IOException {
        String key = "e8d62d019ec15f02480fff030e52db42";
        String sec = "eddf5053c29a30136cc954f41d49df65";
        Api_Client api = new Api_Client(key,sec);
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("units", "1000.00000"); //소수점 4자리 맞추기
        rgParams.put("order_currency", "LBL"); //매매 하려는 코인 이름
        rgParams.put("payment_currency", "KRW"); // 매매하려는 통화
        String result = api.callApi("/trade/market_buy", rgParams);
        System.out.println(result);
    }
    public static void sell_coin() throws IOException {
        Api_Client api = new Api_Client(key,sec);
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("units", "1000.0000"); //소수점 4자리 맞추기
        rgParams.put("order_currency", "LBL"); //매매 하려는 코인 이름
        rgParams.put("payment_currency", "KRW"); //매매하려는 통화
        String result = api.callApi("/trade/market_sell", rgParams);
    }
    public static void  threadTest() {
        lock.lock();
        try {
            threadList.add(1);
        } finally {
            lock.unlock();
        }
    }
    public static void main(String args[]) throws JSONException, IOException, InterruptedException {
//        transactionHistory();
//        getUserHistory();
//        userTradeHistory();
        TransactionHistoryRepository transactionHistoryRepository;
        String key = "e8d62d019ec15f02480fff030e52db42";
        String sec = "eddf5053c29a30136cc954f41d49df65";
        int numThreads = Runtime.getRuntime().availableProcessors(); // 사용 가능한 프로세서 수로 스레드 풀 크기 결정

        ExecutorService executor = Executors.newFixedThreadPool(numThreads*2); //최적의 스레드 풀 개수는?
        GetTransactionHistory getTransactionHistory = new GetTransactionHistory(key,sec,200);
        String[] coinName = {
                "ALEX", "MATIC", "STPT", "VIX", "APT", "STG", "SHIB", "IOTX", "GLM", "FRONT",
                "AUDIO", "WLD", "STX", "ZBC", "ZRX", "META", "BSV", "AQT", "IOST", "BCH",
                "SUI", "CAKE", "JST", "SUN", "ASTR", "GMT", "BTC", "RSR", "ARB", "GMX",
                "BTG", "SEI", "SOFI", "ARK", "MIX", "BTT", "WEMIX", "XPLA", "ONG", "ANKR",
                "SUSHI", "ALGO", "FLR", "BIGTIME", "ONT", "T", "CFX", "FLZ", "VELO", "XPR",
                "ASM", "SFP", "ZTX", "ACE", "MANA", "JUP", "ACH", "RSS3", "BEL", "MINA",
                "CSPR", "TIA", "ACS", "NMR", "STAT", "BIOT", "WOM", "MKR", "WOO", "REI",
                "BLUR", "ELF", "ADA", "VALOR", "BFC", "ICX", "REQ", "STORJ", "LOOM", "DOGE",
                "SXP", "HBAR", "RVN", "CHR", "ADP", "MLK", "PEPE", "WAVES", "CHZ", "XRP",
                "CTSI", "JASMY", "FLOKI", "SAND", "KAVA", "C98", "OSMO", "OCEAN", "EL", "UMA",
                "STRAX", "GAL", "GAS", "THETA", "ENJ", "OAS", "ORC", "XCN", "QTCON", "SIX",
                "GRT", "TFUEL", "WIKEN", "MASK", "UNI", "AAVE", "FX", "NPT", "CKB", "YFI",
                "XTZ", "MOC", "AGI", "EOS", "XEC", "GTC", "UOS", "ENTC", "YGG", "ZIL",
                "AXS", "HOOK", "CTXC", "VRA", "FLOW", "COMP", "STEEM", "XVS", "WAXL", "HFT",
                "ID", "BORA", "LEVER", "DOT", "1INCH", "CRTS", "AVAX", "FNSA", "AZIT", "MAP",
                "FTM", "NCT", "POWR", "KNC", "MAV", "RLC", "ARKM", "BLY", "ATOM", "PENDLE",
                "ORBS", "HIVE", "LPT", "BOBA", "WNCG", "MBL", "OBSR", "SNT", "SNX", "RLY",
                "RDNT", "MBX", "CON", "COS", "API3", "PYR", "WAXP", "DAI", "SPURS", "ONIT",
                "SOL", "DAO", "DAR", "FET", "ETC", "CELR", "OGN", "BNB", "ETH", "NEO",
                "KLAY", "CELO", "LRC", "MANTA", "DATE", "LM", "HIGH", "VET", "MTL", "FITFI",
                "BNT", "ALT", "USDT", "OXT", "FANC", "POLA", "ILV", "BOA", "GHX", "LBL",
                "EDU", "TRX", "NFT", "JOE", "AERGO", "GRACY", "AMO", "ROA", "HIFI", "LSK",
                "TEMCO", "MED", "SWAP", "IMX", "EGLD", "USDC", "MVC", "MEV", "PUNDIX", "GRND",
                "FXS", "CRO", "PLA", "INJ", "CRV", "EVZ", "DYDX", "FLUX", "AGIX", "RPL",
                "LDO", "MAGIC", "ALICE", "XLM", "LINK", "QTUM", "CYBER", "OP", "EGG", "KSM",
                "STMX", "RNDR", "BAL", "GALA", "FIT", "CTC", "RAD", "BAT", "DVI", "APE",
                "MXC", "SSX", "FCT2", "CTK", "ARPA", "TDROP", "APM", "COTI", "TAVA"
        };

        // Print the array to verify the contents
        int idx = 0;
//        try {
//            CompletableFuture<?>[] futures = Arrays.stream(coinName)
//                    .map(coin -> CompletableFuture.runAsync(() -> {
//                        try {
//                            getTransactionHistory.getUserAsset(coin, 1);
//                        } catch (Exception e) {
//                            System.out.println("현재 코인량 조회 중 에러: " + e);
//                        }
//                    }, executor))
//                    .toArray(CompletableFuture[]::new);
//
//            // 모든 작업이 완료될 때까지 기다림
//            CompletableFuture.allOf(futures).join();
//
//        } finally {
//            executor.shutdown(); // 작업 완료 후 스레드 풀 종료
//        }


        for (int i = 0 ; i<1000000;i++) {
            executor.submit(() -> {
                try {
                    threadTest();
                } catch (Exception e) {
                    System.out.println("스레드에러: " + e);
                }
            });

        }
        executor.shutdown();
        // 모든 작업이 완료될 때까지 대기(매우 매우 중요한 코드 --> 이것이 없으면 스레드 처리 씹힘)
        if (executor.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES)) {
            System.out.println("All tasks completed.");
        } else {
            System.out.println("Timeout occurred before termination.");
        }

        // 결과 출력
        System.out.println(threadList.size());
//
//        // 모든 작업이 완료될 때까지 기다린 후 종료
//        executor.shutdown();
//        try {
//            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
//                executor.shutdownNow(); // 시간 초과 시 강제 종료
//            }
//        } catch (InterruptedException e) {
//            executor.shutdownNow();
//        }

//        for( String ans:answer){
//            System.out.println(ans);
//        }


       // buy_coin();
        //System.out.println(getUserCash());
    }
}
