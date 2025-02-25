package com.korea.shop.controller;


import com.google.gson.Gson;
import com.korea.shop.dto.MemberDTO;
import com.korea.shop.dto.login.LoginRequest;
import com.korea.shop.dto.login.LoginResponse;
import com.korea.shop.service.MemberService;
import com.korea.shop.util.CustomJWTException;
import com.korea.shop.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Log4j2
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final JWTUtil jwtUtil; // 의존성 주입

    @PostMapping("/login")
    public void login(@RequestBody Map<String, String> loginRequest, HttpServletResponse response){

        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        try {

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email,password);

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Success 역할
            // 인증 정보를 SecurityContextContext(인증 정보 저장하는 메모리 공간)에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 현재 인증된 사용자 정보 가져오기
            MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal(); // 현재 로그인한 사용자 정보
            Map<String, Object> claims = memberDTO.getClaims(); // 사용자의 추가정보
            Map<String, Object> responseMap = new HashMap<>(claims); // 원본 객체 복사하여 원본 객체 값 보존
            responseMap.put("accessToken",jwtUtil.generateToken(claims, 10));
            responseMap.put("refreshToken",jwtUtil.generateToken(claims, 60*24));

            String jsonStr = new Gson().toJson(responseMap);

            response.setContentType("application/json;charset=UTF-8"); // 한글인코딩
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(jsonStr);


        }catch (BadCredentialsException e){
            // 401 코드 : 인증되지 않은 요청
            log.error("❌ --- [AuthController] --- 이메일 또는 비밀번호 오류 : " + email, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        } catch (Exception e) {
            log.error("❌ --- [AuthController] --- 로그인 과정 중 예기치 못한 오류 ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    // 리액트 요청할때도 members 로 수정해야 함
    @RequestMapping("/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader,
                                       @RequestParam String refreshToken){
        // refreshToken이 없을때 예외발생
        if (refreshToken == null){
            throw new CustomJWTException("NULL_REFRESH");
        }

        // refreshToken이 없거나 토큰이 정상이 아닐때 7글자 미만 예외발생
        if (authHeader == null || authHeader.length() < 7){
            throw new CustomJWTException("INVAILID_STRING");
        }

        String accessToken = authHeader.substring(7); // 토큰 추출

        // Access Token이 만료되지 않았으면?
        if ( !CheckExpiredToken(accessToken) ){ // 정상이면

            // accessToken과 refreshToken을 기존것을 리턴함
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // Refresh Token 검증 & 재발급
        Map<String, Object> claims = jwtUtil.validateToken(refreshToken);
        String newAccessToken = jwtUtil.generateToken(claims, 10);
        String newRefreshToken = checkTime((Integer)claims.get("exp"))
                ? jwtUtil.generateToken(claims, 60*24)
                : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    // 시간이 1시간 미만으로 남았다면
//    private boolean checkTime(Long exp){ // 밀리세컨트 time데이터임
//
//
//
//        // 현재 시간과의 차이 계산 - 밀리세컨즈(현재시간)
//        long gap = exp - System.currentTimeMillis();
//
//        // 분단위 계산
//        long leftMin = gap / (1000*60);
//
//        // 1시간 미만으로 남았는지
//        return leftMin < 60;
//    }

    // 토큰이 만료 되었는지 확인
    private boolean CheckExpiredToken(String token) {

        try {
            jwtUtil.validateToken(token);
            return false; // 정상 토큰이면 false 반환
        }catch (ExpiredJwtException ex){
            return true; // 만료된 토큰이면 true 반환
        } catch (CustomJWTException ex) {
            return true; // 기타 jwt 관련 예외면 true 반환
        }

    }

    private boolean checkTime(Integer exp) {
        java.util.Date expDate = new java.util.Date((long) exp * 1000);
        // 현재 시간과의 차이 계산 - 밀리세컨즈(현재시간)
        long gap = expDate.getTime() - System.currentTimeMillis();
        // 분단위 계산
        long leftMin = gap / (1000 * 60);
        // 1시간 미만으로 남았는지
        return leftMin < 60;
    }
}
