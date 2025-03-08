package com.example.coin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ExcelController {
    private  final TransactionHistoryService service;
    @GetMapping("/excel")
    public String main() { // 1
        return "excelInsert";
    }


    @PostMapping("/excel/read")
    public String readExcel(@RequestParam("file") MultipartFile file, Model model, HttpServletRequest request)

            throws IOException, JSONException { // 2

        HttpSession session = request.getSession();
        String userId = String.valueOf(session.getAttribute("ID"));
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()); // 3

        if (!extension.equals("xlsx") && !extension.equals("xls")) {
            throw new IOException("엑셀파일만 업로드 해주세요.");
        }

        Workbook workbook = null;

        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (extension.equals("xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        }

        Sheet worksheet = workbook.getSheetAt(0);
        List<TransactionHistory> ans = service.getUserTrade(userId); //회원의 거래기록
        Map<String,String> memo = new HashMap<>();//DB 테이블에 있는 행을 저장함
        System.out.println("총 행 개수: "+worksheet.getPhysicalNumberOfRows());
        long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기

        for(int i = 0; i<ans.size();i++){
           String hashKey =  ans.get(i).getCoinName() +
                   ans.get(i).getState() + ans.get(i).getCoinDate();
           memo.put(hashKey,"A");
        }
        //넣을 때 역순으로 넣기

        int idx =  worksheet.getPhysicalNumberOfRows()-1;
//        for (int i = worksheet.getPhysicalNumberOfRows()-1; i >2 ; i--) { //

        //한번에 매매(같은 시간에 매매)됬지만 n번으로 분할되어 매매되었을 때 하나로 묶기
        Map <String,List<String>> unionDuple = new HashMap<>();  //엑셀데이터에서 같은값 하나로 묶기위해
        //
        while (idx >2){
            Row row = worksheet.getRow(idx--);
            String coinState = row.getCell(2).getStringCellValue();
            if (coinState.length() ==4){
                if  (coinState.substring(2).equals("매수")) {
                    coinState ="매수";
                }
                else if  (coinState.substring(2).equals("매도")){
                    coinState ="매도";
                }
            }
            String hashKey = row.getCell(1).getStringCellValue()+
                    coinState+
                    row.getCell(0).getStringCellValue();
            List<String> mapValues = new ArrayList<>();
            if(memo.containsKey(hashKey)){ //중복된 값이 있으면 넣지 않기
                continue;
            }
            String coinDate = row.getCell(0).getStringCellValue();
            String coinName = row.getCell(1).getStringCellValue();
            String tempTradeAmount = row.getCell(3).getStringCellValue();
            String tempTradePrice = row.getCell(4).getStringCellValue();
            String fee = row.getCell(6).getStringCellValue();
            String tempAfterPrice = row.getCell(7).getStringCellValue();

            tempTradeAmount = tempTradeAmount.split(" ")[0].replace(",",""); //거래량 뒤에 krw 문자열 제외
            tempTradePrice = tempTradePrice.split(" ")[0].replace(",","");  //체결가격 뒤에 krw 문자열 제외
            tempAfterPrice = tempAfterPrice.split(" ")[0].replace(",","");  // 정산금약 뒤에 krw 문자열 제외
            mapValues.add(coinDate);    //0
            mapValues.add(coinName);    //1
            mapValues.add(coinState);   //2
            mapValues.add(tempTradeAmount);   //3 !
            mapValues.add(tempTradePrice);      //4!
            mapValues.add(fee);         //5
            mapValues.add(tempAfterPrice); //6 !
            if(unionDuple.containsKey(hashKey)){ //중복된 값(같은 매매인데 n번 체결된것)
                //값 갱신 해주기
                List<String> temp = unionDuple.get(hashKey);
                Double TradeAmount = Double.valueOf(temp.get(3))+ Double.valueOf(mapValues.get(3));
                temp.set(3,String.valueOf(TradeAmount));

//                Double TradePrice = Double.valueOf(temp.get(4))+Double.valueOf(mapValues.get(4));
//                temp.set(4,String.valueOf(TradePrice));
                Double AfterPrice = 0.0;
                if(temp.get(6).substring(0,1).equals("+")){

                     AfterPrice= Double.valueOf(temp.get(6).substring(1))+
                            Double.valueOf(mapValues.get(6).substring(1));
                }
                else if(temp.get(6).substring(0,1).equals("-")){
                     AfterPrice= -Double.valueOf(temp.get(6).substring(1))-
                            Double.valueOf(mapValues.get(6).substring(1));
                }
                if(AfterPrice> 0.0 ){
                    temp.set(6,"+"+String.valueOf(AfterPrice));

                }else if(AfterPrice < 0.0){
                    temp.set(6,"-"+String.valueOf(AfterPrice));
                }else {
                    temp.set(6,"+0");
                }
                continue;
            }
            unionDuple.put(hashKey,mapValues);
        }
        for(List<String> li : unionDuple.values()){
            TransactionHistory e =new TransactionHistory();
            e.setUser(userId);
            e.setCoinDate( li.get(0));
            e.setCoinName( li.get(1));
            e.setState( li.get(2)); //상태
            e.setAmount( li.get(3));//거래수량
            e.setPrice( li.get(4)); //체결가격
            e.setFee( li.get(5)); //수수료
            e.setAfterTrade(li.get(6)); //정산 금액
            service.excelDataSave(e);
        }

        List<TransactionHistory> tradeInfo = service.getUserTrade(userId);
        List<TransactionHistory>  userOwnCoin = new ArrayList<>(); //사용자가 보유한 코인
        //내가 보유한 코인 불러오는 로직
        double trade_cnt = 0.0 ; // 총 매매 횟수
        double win_cnt = 0.0;
        for(int indx = 0;indx < tradeInfo.size();indx++ ){
            if(tradeInfo.get(indx).getState().equals("매수")){
                userOwnCoin.add(tradeInfo.get(indx));
                trade_cnt++;
            }
            else if (tradeInfo.get(indx).getState().equals("매도")) {
                for(int i = 0 ; i<userOwnCoin.size();i++){
                    if(tradeInfo.get(indx).getCoinName().equals(userOwnCoin.get(i).getCoinName())){
                        String sellPrice  = tradeInfo.get(indx).getAfterTrade().substring(1);
                        String buyPrice = userOwnCoin.get(i).getAfterTrade().substring(1);
                        if(Double.valueOf(sellPrice)>Double.valueOf(buyPrice)){
                            win_cnt++;
                        }
                        userOwnCoin.remove(i);
                        break;
                    }
                }
            }
        }
        List<String> myCoin = new ArrayList<>();
        Double myAllAsset = 0.0; //내 총자산 (내가 보유한 모든 코인가격 누적합);
        for(int i = 0 ; i< userOwnCoin.size();i++){  //
            myAllAsset += Double.valueOf(userOwnCoin.get(i).getAmount()) * Double.valueOf(userOwnCoin.get(i).getPrice());
        }
//        for(int i = 0 ; i< userOwnCoin.size();i++){
//            myCoin.add(userOwnCoin.get(i).getCoinName());  //
//            Double coinAmount = Double.valueOf(userOwnCoin.get(i).getAmount()) * Double.valueOf(userOwnCoin.get(i).getPrice());
//            coinAmount = coinAmount/myAllAsset;
//            DecimalFormat decimalFormat = new DecimalFormat("#.#"); //소수점 첫째자리까지만 짜름
//            myCoin.add(decimalFormat.format(coinAmount));
//        }



        System.out.println("정답: 승률: " + win_cnt/trade_cnt*100 +"%");
        List<user_info>  user = service.getUserData(userId);
        getUserCash uC = new getUserCash(user.get(0).getApi_key(),user.get(0).getSec_key());
        String total_krw = uC.getCash(); //유저가 가진 현금량
        myAllAsset+=Double.valueOf(total_krw);
        List<user_rank> userRank = service.getUserRank(userId);
        user_rank uR = userRank.get(0);
        uR.setWin_rate(String.valueOf(win_cnt/trade_cnt*100)); //승률을 넣는 코드
        uR.setUser_amount(String.valueOf(myAllAsset));  //총자산을 넣는 코드
        service.registUserRanking(uR);  //승률, 총자산, 수익률 update하는 코드
        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
        System.out.println("시간차이(m) : "+secDiffTime);
        return "redirect:/login";
    }
}
