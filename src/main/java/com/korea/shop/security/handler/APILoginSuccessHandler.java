package com.korea.shop.security.handler;

import com.google.gson.Gson;
import com.korea.shop.dto.MemberDTO;
import com.korea.shop.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler{

  private final JWTUtil jwtUtil;

@Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException { // 요청, 응답, 인증된사용자

    log.info("-------------------------------------");
    log.info(authentication); // 토큰정보와 계정 아이디, 비밀번호 그리고 권한정보 까지 확인 가능함
    log.info("-------------------------------------");

    // 인증된 사용자 정보 -> DTO로 받아옴 -> MAP -> JSON으로 변환
    // 로그인 성공하면 토큰 발급해서 응답객체에 담아 클라이언트에게 보냄
    
    // 현재 인증된 사용자, 클라이언트에서 넘어온 로그인 계정이므로 DTO로 받음
    MemberDTO memberDTO = (MemberDTO)authentication.getPrincipal(); 
    
    // 사용자 정보 map 형식 변환
    Map<String, Object> claims = memberDTO.getClaims();
    String username = memberDTO.getUsername(); // 이부분 내가 수정함 02-21

    // 최초 생성 토큰 유효시간 10분 | 이부분 내가 수정함 02-21
    String accessToken = JWTUtil.generateToken(username, claims, 10);
    // 갱신할때 사용 24시간 | 이부분 내가 수정함 02-21
    String refreshToken = JWTUtil.generateToken(username, claims,60*24);

    // 토큰 데이터 추가
    claims.put("accessToken", accessToken); // 승인토큰
    claims.put("refreshToken", refreshToken); // 재승인토큰

    Gson gson = new Gson();

    // Map 객체를 JSON으로 변환
    String jsonStr = gson.toJson(claims);

    // "Context-type : application/json"
    response.setContentType("application/json; charset=UTF-8");
  
    // Json 데이터 출력 - 인증된 사용자 정보가 Json형태로 반환
    PrintWriter printWriter = response.getWriter(); // HTTP응답을 출력할 PrintWriter 객체 생성
    printWriter.println(jsonStr); // 클라이언트에게 반환
    printWriter.close(); // 출력 닫음, 토큰 반환

  }

  
}
