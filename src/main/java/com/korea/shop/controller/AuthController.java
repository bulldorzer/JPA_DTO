package com.korea.shop.controller;


import com.korea.shop.dto.MemberDTO;
import com.korea.shop.dto.login.LoginRequest;
import com.korea.shop.dto.login.LoginResponse;
import com.korea.shop.util.CustomJWTException;
import com.korea.shop.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil; // 의존성 주입

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Success 역할
            // 인증 정보를 SecurityContextContext(인증 정보 저장하는 메모리 공간)에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 현재 인증된 사용자 정보 가져오기
            MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal(); // 현재 로그인한 사용자 정보
            Map<String, Object> claims = memberDTO.getClaims(); // 사용자의 추가정보
            String usename = memberDTO.getUsername();

            // JWT 토큰 생성
            String accessToken = jwtUtil.generateToken(usename, claims, 10);
            String refreshToken = jwtUtil.generateToken(usename, claims, 60*24);

            //응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("username", memberDTO.getUsername());
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("role", memberDTO.getRoleNames());

            return ResponseEntity.ok(response); // JSON 응답 변환
        }catch (BadCredentialsException e){
            // fail 역할
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    // 리액트 요청할때도 members 로 수정해야 함
    @RequestMapping("/api/members/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader,
                                       @RequestHeader("Refresh-Token") String refreshToken){
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
        Claims claims = JWTUtil.validateToken(refreshToken);
        String username = claims.getSubject();
        long expMillis = claims.getExpiration().getTime();

        // 새로운 accessToken 수명 10분으로 새로 생성
        String newAccessToken = jwtUtil.generateToken(username, claims,10);


        String newRefreshToken = checkTime( expMillis ) == true ?
                jwtUtil.generateToken(username,claims,60*24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    // 시간이 1시간 미만으로 남았다면
    private boolean checkTime(Long exp){ // 밀리세컨트 time데이터임



        // 현재 시간과의 차이 계산 - 밀리세컨즈(현재시간)
        long gap = exp - System.currentTimeMillis();

        // 분단위 계산
        long leftMin = gap / (1000*60);

        // 1시간 미만으로 남았는지
        return leftMin < 60;
    }

    // 토큰이 만료 되었는지 확인
    private boolean CheckExpiredToken(String token) {

        try {
            JWTUtil.validateToken(token);
            return false; // 정상 토큰이면 false 반환
        }catch (ExpiredJwtException ex){
            return true; // 만료된 토큰이면 true 반환
        } catch (CustomJWTException ex) {
            return true; // 기타 jwt 관련 예외면 true 반환
        }

    }
}
