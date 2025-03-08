package com.example.coin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class statisticsController {
    private  final interestService service;
    @GetMapping (value ="/login/statistics")
    public String userRank()
    {
       return "statistics" ;
    }
    @GetMapping(value = "login/coinInterest/{id}")
    public  void esfj(@PathVariable String id, HttpServletRequest request,HttpServletResponse response) throws IOException {
        System.out.println("get");
        HttpSession session = request.getSession();
        String userId = String.valueOf(session.getAttribute("ID"));
        //중복 처리해보기
        List<interest> check = service.checkDuple(userId,id);  //중복값 체크
        if(check.size() == 0){  //DB에 관심종목이 없을 때 추가
            interest i = new interest();
            i.setCoinName(id);
            i.setUserName(userId);
            service.save(i);
        }


        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Location", "/login/search"); //이동할 url 경로
        response.setHeader("Content-Type", "text/html");
        response.getWriter().println("<html><body><script>location.href='/login/search'</script></body></html>");


    }


}
