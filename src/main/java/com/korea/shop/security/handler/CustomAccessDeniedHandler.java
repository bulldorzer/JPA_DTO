package com.korea.shop.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        Gson gson =new Gson();
        // Map 데이터를 JSON 형식으로 변환
        String jsonStr = gson.toJson(Map.of("error", "ERROR_ACCESSDENIED"));

        response.setContentType("application/json"); // json 타입으로 설정
        response.setStatus(HttpStatus.FORBIDDEN.value()); // 403
        // Http 상태 : 403 코드 반환 - 권한없어서 접근 금지라는 뜻
        PrintWriter printWriter = response.getWriter(); // HTTP응답을 출력할 PrintWriter 객체 생성
        printWriter.println(jsonStr); // 클라이언트에게 반환
        printWriter.close(); // 출력 닫음, 토큰 반환

    }
}
