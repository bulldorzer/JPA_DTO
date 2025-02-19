package com.korea.shop.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Log4j2
public class JWTUtil {

  // 시크릿 키 설정 (32bit 이상 필요)
  // JWT 서명을 위한 비밀키
  private static String key = "1234567890123456789012345678901234567890";

  // JWT 생성 메서드
  // valueMap : 사용자 정보, min: 토큰 유효시간
  public static String generateToken(Map<String, Object> valueMap, int min) {

    SecretKey key = null;
    try {
      // HMAC-SHA 알고리즘을 이용하여 비밀키 생성 -> UTF-8형식으로 변환
      key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
    }catch (Exception e){
      throw new RuntimeException(e.getMessage());
    }

    // 토큰 객체 생성
    String jwtStr = Jwts.builder() // JWT API를 통해 가져온 Jwts 객체
            .setHeader(Map.of("typ","JWT")) // 헤더 설정
            .setClaims(valueMap) // 사용자 정보 추가
            .setIssuedAt(Date.from(ZonedDateTime.now().toInstant())) // 발급시간
            .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant())) // 만료시간
            .signWith(key) // 서명추가
            .compact();
    return jwtStr;

  }

  // 토큰 유효성 검사
  // 파싱 : 데이터를 분석하고 분해해서 원하는 형태로 변환하는 과정
  public static Map<String, Object> validateToken(String token) {

    Map<String, Object> claim = null;
    
    try {

      // HMAC-SHA 알고리즘을 이용하여 비밀키 생성 -> UTF-8형식으로 변환
      SecretKey key =Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

      claim = Jwts.parserBuilder() // 분해 해서 분석
              .setSigningKey(key) // 서명 검증
              .build()
              .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
              .getBody();
      
      
    } catch (MalformedJwtException malformedJwtException) { // jwt 형식이 올바르지 않음
      throw new CustomJWTException("MalFormed") ;
    } catch (ExpiredJwtException expiredJwtException){ // 토큰이 만료됨
      throw new CustomJWTException("Expired") ;
    } catch (InvalidClaimException invalidClaimException){ // 클레임 정보가 올바르지 않음
      throw new CustomJWTException("Invalid") ;
    } catch (JwtException jwtException){ // 기타 jwt 관련 예외
      throw new CustomJWTException("JWTError") ;
    } catch (Exception e){ // 기타 알수 없는 예외
      throw new CustomJWTException("Error") ;
    }
    
    return claim;

  }

}
