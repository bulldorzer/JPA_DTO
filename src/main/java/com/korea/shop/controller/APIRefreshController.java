package com.korea.shop.controller;

import com.korea.shop.util.CustomJWTException;
import com.korea.shop.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class APIRefreshController {

    // 리액트 요청할때도 members 로 수정해야 함
    @RequestMapping("/api/members/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader,
                                       String refreshToken){
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
        if ( CheckExpiredToken(accessToken) == false ){

            // accessToken과 refreshToken을 기존것을 리턴함
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // Refresh Token 검증
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);

        // 새로운 accessToken 수명 10분으로 새로 생성
        String newAccessToken = JWTUtil.generateToken(claims,10);

        String newRefreshToken = checkTime( (Integer) claims.get("exp")) == true ?
                JWTUtil.generateToken(claims,60*24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    // 시간이 1시간 미만으로 남았다면
    private boolean checkTime(Integer exp){

        // JWT exp를 날짜로 변환 - (만료시간)
        java.util.Date expDate = new java.util.Date( (long)exp * (1000));

        // 현재 시간과의 차이 계산 - 밀리세컨즈(현재시간)
        long gap = expDate.getTime() - System.currentTimeMillis();

        // 분단위 계산
        long leftMin = gap / (1000*60);

        return leftMin < 60;
    }

    // 토큰이 만료 되었는지 확인
    private boolean CheckExpiredToken(String token) {

        try {
            JWTUtil.validateToken(token);
        }catch (CustomJWTException ex){
            if (ex.getMessage().equals("Expired")){
                return true;
            }
        }
        return false;
    }

}
