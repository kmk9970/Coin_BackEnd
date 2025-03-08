//package com.example.coin;
//
//import com.example.coin.entity.CoinName;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.json.JSONException;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//@Slf4j
//@RequiredArgsConstructor
//public class TransactionHistoryController {
//
//    private String coinName = "";
//    private  final TradeService tradeService;
//    private  final TransactionHistoryService service;
//    @GetMapping (value = "/drills")
//    public String aa(){
//        return "apitest";
//    }
//    @GetMapping (value = "/")
//    public  String loginPage(Model model) {
//        model.addAttribute("LoginForm", new LoginForm());
//
//        List <user_info> userInfo = service.getUserData("조승빈");
//        for(int i =0; i<userInfo.size();i++){
//            System.out.println(userInfo.get(i).getApi_key());
//            System.out.println(userInfo.get(i).getSec_key());
//        }
//        return "loginPage";
//    }
//    @PostMapping(value = "/")
//    public void postLoginPage(HttpServletRequest request){
//        HttpSession session = request.getSession(false); // 세션이 없으면 새로 생성하지 않음
//        if (session != null) {
//            session.invalidate(); // 세션 종료
//        }
//    }
//
//    @GetMapping (value="/createMember")
//    public  String createMember(Model model) {
//        model.addAttribute("CreateMemberForm",new CreateMemberForm());
//        return "createMember";
//    }
//    @PostMapping (value="/createMember")
//    public String postCreateMember(@Valid CreateMemberForm form, BindingResult result){
//        if (result.hasErrors()) {
//            //serviceTest.aaa();
//            System.out.println(result.hasErrors());
//            return "create";
//        }
//
//        //비밀번호와 비밀번호 재확인 일치하지 않으면 다시
//        if (!form.getMakePass().equals(form.getConfirm())) {
//            return "create";
//        }
//
//        //중복되는 아이디가 있으면 다시
////        List<user> idInfo = service.checkDupleId();
////        for (int i = 0; i < idInfo.size(); i++) {
////            if (form.getMakeId().equals(idInfo.get(i).getIdentity())) {
////                return "redirect:/createMember";
////            }
////        }
//
//        //데이터 insert 코드
//        user_info u = new user_info();  //엔티티 객체 생성
//        u.setIdentity(form.getMakeId());
//        u.setPass(form.getMakePass());
//        u.setApi_key(form.getApi_key());
//        u.setSec_key(form.getSec_key());
//        service.registMember(u);//회원정보 테이블에 저장
//
//        user_rank uR = new user_rank();
//        uR.setUser_id(form.getMakeId());
//        service.registUserRanking(uR);//회원랭킹 테이블에 저장
//        return "redirect:/";
//    }
//    @GetMapping(value = "/test")
//    public String a (){
//        List<user_info> allUser = service.getAllUserData();
//        List<coin_info>  allCoin = service.getCoinState();
//        List<String> coinName  = new ArrayList<>();
//        for(int i = 0; i<allCoin.size();i++){
//            coinName.add(allCoin.get(i).getCoinName());
//        }
//        //10분에 한번씩 랜덤하게 코인 매매하는 코드
//        AutoTrading aT = new AutoTrading(allUser,coinName);
//        aT.autoRandomTrade();
//        return "test";
//    }
//    @GetMapping (value = "/login")
//    public String getMainPage(HttpServletRequest request) throws JSONException, IOException {
//        AllCoin a = new AllCoin( "https://api.bithumb.com/public/ticker/ALL_KRW");
//        List <String> cN = a.allCoin();  //
//
//        //코인 이름을 넣는 코드
//        //데이터베이스에 저장된 코인수와 api로 불러온 코인수가 다르면 갱신
//        if(service.getCoinSize() != cN.size()){
//            service.delCoinName();
//            for(int i = 0 ;i<cN.size();i++){
//                CoinName cT = new CoinName();
//                cT.setCoinName(cN.get(i));
//               service.save(cT);
//            }
//        }
//
//        List<List<String>> allCoinState =  a.getAllCoinState(cN);
//        if(service.getCoinState().size() !=allCoinState.size()) {
//            service.delCoinInfo();
//            for (int x = 0; x < allCoinState.size(); x++) {
//                coin_info c = new coin_info();
//                c.setCoinName(cN.get(x));
//                c.setOpening_price(allCoinState.get(x).get(0));
//                c.setMax_price(allCoinState.get(x).get(1));
//                c.setMin_price(allCoinState.get(x).get(2));
//                c.setUnites_traded(allCoinState.get(x).get(3));
//                c.setFluctate_24H(allCoinState.get(x).get(4));
//                c.setFluctate_rate_24H(allCoinState.get(x).get(5));
//                c.setPrev_closing_price(allCoinState.get(x).get(6));
//                c.setClosing_price(allCoinState.get(x).get(7));
//                c.setUnits_traded_24H(allCoinState.get(x).get(8));
//                c.setAcc_trade_value_24H(allCoinState.get(x).get(9));
//                c.setAcc_trade_value(allCoinState.get(x).get(10));
//                service.coinStateSave(c);
//            }
//        }
//        HttpSession session = request.getSession();
//        String userId = String.valueOf(session.getAttribute("ID"));
//        List <user_info> userInfo = service.getUserData(userId);
//        //회원들의 코인을 출력하는 코드
//
//        List<coin_info> coinName = service.getCoinState();
//        GetTransactionHistory h =
//                new GetTransactionHistory(userInfo.get(0).getApi_key(),userInfo.get(0).getSec_key(),coinName.size());
//        List<String> coinN = new ArrayList<>();
//        for(int i =0;i<coinName.size();i++){
//            coinN.add(coinName.get(i).getCoinName());
//        }
//
//        long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
//
////실험할 코드 추가
//        try {
//            //내가 보유한 코인 불러오는 코드
//           // myCoin =  h.executeAnsConcurrently(coinN);
//
//        }catch (Exception e){
//            System.out.println("ㅈㅇㅁㅈㅇ에러"+e);
//        }
//        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
//        long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
//        System.out.println("시간차이(m) : "+secDiffTime);
//
//
//
//
//        return "mainPage";
//    }
//    @PostMapping (value = "/login")
//    //public String  postMainPage(user_info u , BindingResult result, HttpServletRequest request){
//    public String  postMainPage(@Valid LoginForm form , BindingResult result, HttpServletRequest request,HttpServletResponse response) throws IOException {
//        if (result.hasErrors()) {
//            System.out.println(result.hasErrors());
//            response.setStatus(HttpServletResponse.SC_OK);
//            response.setHeader("Location", "/login/search"); //이동할 url 경로
//            response.setHeader("Content-Type", "text/html");
//            response.getWriter().println("<html><body><script>location.href='/'</script></body></html>");
//            //return "re`direct:/";
//        }
//        List<user_info> loginCheck =service.loginCheck(form.getIdentity(), form.getPass());
//        //로그인 확인 : 아이디,비번이 일치하면 loginCheck 사이즈가 1 , 일치하지 않으면 0
//        if(loginCheck.size() == 0 ){
//            //로그인 정보가 일치 하지 않으면 로그인 실패
//            response.setStatus(HttpServletResponse.SC_OK);
//            response.setHeader("Location", "/login/search"); //이동할 url 경로
//            response.setHeader("Content-Type", "text/html");
//            response.getWriter().println("<html><body><script>location.href='/'</script></body></html>");
//            return "redirect:/";
//        }
//        String identity =service.getUserData(form.getIdentity()).get(0).getIdentity();
//        HttpSession session = request.getSession();
//        session.setAttribute("ID", form.getIdentity()); //세션
//        String userId = String.valueOf(session.getAttribute("ID"));
//        System.out.println("로그인 성공 : "+userId);
//        return "redirect:/login";
//    }
//
//    @GetMapping (value = "/login/myasset")
//    public String showMyAsset(){
//
//
//
//        return "myasset";
//    }
//
//    @PostMapping(value = "/login/myasset")
//    @ResponseBody
//    public List<String> sendMyAsset(HttpServletRequest request) throws JSONException, IOException {
//        HttpSession session = request.getSession();
//        String userId = String.valueOf(session.getAttribute("ID"));
//        List<TransactionHistory> tradeInfo = service.getUserTrade(userId);
//        List<TransactionHistory>  userOwnCoin = new ArrayList<>(); //사용자가 보유한 코인
//        //내가 보유한 코인 불러오는 로직
//        double trade_cnt = 0.0 ; // 총 매매 횟수
//        double win_cnt = 0.0;
//        for(int idx = 0;idx < tradeInfo.size();idx++ ){
//            if(tradeInfo.get(idx).getState().equals("매수")){
//                userOwnCoin.add(tradeInfo.get(idx));
//                trade_cnt++;
//            }
//            else if (tradeInfo.get(idx).getState().equals("매도")) {
//                for(int i = 0 ; i<userOwnCoin.size();i++){
//                    if(tradeInfo.get(idx).getCoinName().equals(userOwnCoin.get(i).getCoinName())){
//                        String sellPrice  = tradeInfo.get(idx).getAfterTrade().substring(1);
//                        String buyPrice = userOwnCoin.get(i).getAfterTrade().substring(1);
//                        if(Double.valueOf(sellPrice)>Double.valueOf(buyPrice)){
//                            win_cnt++;
//                        }
//                        userOwnCoin.remove(i);
//                        break;
//                    }
//                }
//            }
//        }
//        List<String> myCoin = new ArrayList<>();
//        List<user_info>  user = service.getUserData(userId);
//        getUserCash uC = new getUserCash(user.get(0).getApi_key(),user.get(0).getSec_key());
//        String total_krw = uC.getCash(); //유저가 가진 현금량
//        Double myAllAsset = 0.0; //내 총자산 (내가 보유한 모든 코인가격 누적합 + 현금);
//        List<transaction_log> userAmount = tradeService.getUserAmount(userId);
//        for(int i = 0;i< userAmount.size();i++){
//            myAllAsset+= Double.valueOf(userAmount.get(i).getAmount()) *Double.valueOf(userAmount.get(i).getCoin_price());
//        }
//
//        for(int i = 0 ; i< userOwnCoin.size();i++){
//            myAllAsset += Double.valueOf(userOwnCoin.get(i).getAmount()) * Double.valueOf(userOwnCoin.get(i).getPrice());
//        }
//        myAllAsset += Double.valueOf(total_krw);
//        DecimalFormat decimalFormat = new DecimalFormat("#.#");
//        for(int i = 0 ; i< userOwnCoin.size();i++){
//           myCoin.add(userOwnCoin.get(i).getCoinName());
//           Double coinAmount = Double.valueOf(userOwnCoin.get(i).getAmount()) * Double.valueOf(userOwnCoin.get(i).getPrice());
//            coinAmount = coinAmount/myAllAsset;
//           //소수점 첫째자리까지만 짜름
//           myCoin.add(decimalFormat.format(coinAmount));
//        }
//
//        myCoin.add("KRW");
//        myCoin.add(decimalFormat.format(Double.valueOf(total_krw)/myAllAsset));
//        for(int i = 0;i< userAmount.size();i++){
//            myCoin.add(userAmount.get(i).getCoin_name());
//            Double tmp = Double.valueOf(userAmount.get(i).getAmount()) *Double.valueOf(userAmount.get(i).getCoin_price());
//            tmp /=myAllAsset;
//            myCoin.add(String.valueOf(tmp));
//        }
//        List<transaction_log>  trade_log = tradeService.getUserAmount(userId);
//        for(int i = 0;i<trade_log.size();i++){
//
//        }
//        System.out.println("정답: 승률: " + win_cnt/trade_cnt*100 +"%");
//        return myCoin;
//    }
//    @GetMapping (value = "/login/search")
//    public String getCoinSearch(){
//        return "coinSearch";
//    }
//    @PostMapping(value = "/login/search")
//    @ResponseBody
//    public List<coin_info> postCoinSearch(){
//        return service.getCoinState();
//    }
//
//    @GetMapping (value = "/login/graph")  //그래프를 보고싶은 코인이름을 입력받는 화면
//    public String getCoinGraph(Model model){
//        model.addAttribute("CoinInput", new CoinInput());
//        return "coinGraph";
//    }
//    @PostMapping(value = "/login/coinPriceInsert")
//    public String saveCoinPrice(@Valid CoinInput cI) throws JSONException, IOException {
//
//        String url ="https://api.bithumb.com/public/candlestick/"+ cI.getCoinName()+"_KRW/24h";
//        //입력받은 코인의 일별 가격
//        AllCoin a = new AllCoin(url);
//        List<List<String>> coinCadle =  a.getCandleStick();
//
//            this.coinName = cI.getCoinName();
//           List<coin_price> coinPriceInfo = service.getCoinPrice(this.coinName);
//           Map<String,String> verifyCoin = new HashMap<>();
//           for(int i = 0; i<coinPriceInfo.size();i++){
//               verifyCoin.put(coinPriceInfo.get(i).getCoinDate(),"z");
//           }
//         //DB에 해당 코인의 가격 정보가 없으면
//            //고치기
//            //날짜를 가지고 중복되는게 없을 때만
//            List<coin_price> cpInfo = new ArrayList<>();
//            for(int i = 0; i < coinCadle.size(); i++) {
//                if(!verifyCoin.containsKey(coinCadle.get(i).get(0))){
//                    coin_price c = new coin_price();
//                    c.setCoinName(cI.getCoinName());
//                    c.setCoinDate(coinCadle.get(i).get(0));
//                    c.setOpening_price(coinCadle.get(i).get(1));
//                    c.setClosing_price(coinCadle.get(i).get(2));
//                    c.setMax_price(coinCadle.get(i).get(3));
//                    c.setMin_price(coinCadle.get(i).get(4));
//                    c.setUnites_traded(coinCadle.get(i).get(5));
//                    cpInfo.add(c);
//                    //service.dumSave(c);
//                    }
//                }
//            if(cpInfo.size()>0){  //넣을 값이 있을때만 실행
//                service.saveCoinPrice(cpInfo); //배치 인서트 코드
//                //service.bulkCoinPrice(cpInfo); //벌크 인서트 코드
//            }
//
//        return "redirect:/login/showGraph";
//    }
////    @GetMapping (value = "/login/showGraph")
////    public  String showGraph() {;
////        return "showCoinGraph";}
////
////
//    @ResponseBody
//    @RequestMapping (value = "/login/showGraph")
//    public List<Integer>  coinPrice() throws JSONException, IOException {
//
//        String url ="https://api.bithumb.com/public/candlestick/"+ this.coinName+"_KRW/24h";
//        //입력받은 코인의 일별 가격
//
//        AllCoin a = new AllCoin(url);
//        List<List<String>> coinCadle =  a.getCandleStick();
//
//
//        List<coin_price> coinPriceInfo = service.getCoinPrice(this.coinName);
//        Map<String,String> verifyCoin = new HashMap<>();
//        for(int i = 0; i<coinPriceInfo.size();i++){
//            verifyCoin.put(coinPriceInfo.get(i).getCoinDate(),"z");
//        }
//        //DB에 해당 코인의 가격 정보가 없으면
//        //고치기
//        //날짜를 가지고 중복되는게 없을 때만
//        List<coin_price> cpInfo = new ArrayList<>();
//        for(int i = 0; i < coinCadle.size(); i++) {
//            if(!verifyCoin.containsKey(coinCadle.get(i).get(0))){
//                coin_price c = new coin_price();
//                c.setCoinName(this.coinName);
//                c.setCoinDate(coinCadle.get(i).get(0));
//                c.setOpening_price(coinCadle.get(i).get(1));
//                c.setClosing_price(coinCadle.get(i).get(2));
//                c.setMax_price(coinCadle.get(i).get(3));
//                c.setMin_price(coinCadle.get(i).get(4));
//                c.setUnites_traded(coinCadle.get(i).get(5));
//                cpInfo.add(c);
//                //service.dumSave(c);
//            }
//        }
//        if(cpInfo.size()>0){  //넣을 값이 있을때만 실행
//            service.saveCoinPrice(cpInfo); //배치 인서트 코드
//            //service.bulkCoinPrice(cpInfo); //벌크 인서트 코드
//        }
//
//
//        List<coin_price> cP = service.getCoinPrice(this.coinName);
//        List<Integer> ans = new ArrayList<>();
//
//        for(int i = 0; i<cP.size();i++){
//           ans.add(Integer.valueOf((int)Double.parseDouble(cP.get(i).getClosing_price())));
//        }
//        return ans;
//   }
//
//
//    @GetMapping("/login/coinTrade/{id}")
//    public String showContent(@PathVariable String id, Model model) {
//        this.coinName= id;
//        //model.addAttribute("content", boardService.getContent(id));
//        System.out.println("정답"      + this.coinName);
//        return "showCoinGraph";
//    }
//
//
//
//    @ResponseBody
//    @RequestMapping (value = "/login/coinTrade/{id}")
//    public List<Integer>  sfsf() throws JSONException, IOException {
//
//        List<coin_price> cP = service.getCoinPrice(this.coinName);
//        List<Integer> ans = new ArrayList<>();
//
//        for(int i = 0; i<cP.size();i++){
//            ans.add(Integer.valueOf((int)Double.parseDouble(cP.get(i).getClosing_price())));
//        }
//        return ans;
//    }
//}