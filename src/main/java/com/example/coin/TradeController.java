package com.example.coin;

import com.example.coin.service.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TradeController {
    private  final TradeService service;
    @GetMapping (value = "/login/trade")
    public String trade(){
        return "coinTrade";
    }
    @PostMapping("login/buycoin")
    public void WritePurchaseLog(String coin_name, String price, HttpServletRequest request,HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        int success;
        try { //success가 0이면 실패 1이면 성공


            success=service.WritePurchaseLog(coin_name,price,String.valueOf(session.getAttribute("ID")));
            System.out.println("코인사기"+success);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(success==1) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Location", "/login/search"); //이동할 url 경로
            response.setHeader("Content-Type", "text/html");
            response.getWriter().println("<html><body><script>location.href='/login'</script></body></html>");

        }else{
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Location", "/login/search"); //이동할 url 경로
            response.setHeader("Content-Type", "text/html");
            response.getWriter().println("<html><body><script>location.href='/login/search'</script></body></html>");

        }
    }

    @PostMapping("login/sellcoin")
    public void WriteSellLog(String coin_name, String price, HttpServletRequest request,HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        int success;
        try {
            success=service.WriteSellLog(coin_name,price,String.valueOf(session.getAttribute("ID")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if(success==1) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Location", "/login/search"); //이동할 url 경로
            response.setHeader("Content-Type", "text/html");
            response.getWriter().println("<html><body><script>location.href='/login'</script></body></html>");

        }else{
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Location", "/login/search"); //이동할 url 경로
            response.setHeader("Content-Type", "text/html");
            response.getWriter().println("<html><body><script>location.href='/login/search'</script></body></html>");

        }
    }
}
