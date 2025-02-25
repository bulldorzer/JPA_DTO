package com.korea.shop.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component // 컨테이너가 관리하는 클래스 : 의존성 주입이 가능해짐
public class JWTUtil {

  // 시크릿 키 설정 (32bit 이상 필요)
  // JWT 서명을 위한 비밀키
  private SecretKey key;

  public JWTUtil(@Value("${jwt.secret}") String secretKey){
    log.info("=================<JWTUtil>======================");
    byte[] decodeKey = Base64.getDecoder().decode(secretKey); // base64형식으로 디코딩 진행
    if (decodeKey.length<32){
      throw new IllegalArgumentException("JWT Secret Key must be at least 256 bits (32 bytes).");
    }
    // HMAC-SHA 알고리즘을 이용하여 비밀키 생성 -> UTF-8형식으로 변환
    this.key= Keys.hmacShaKeyFor(decodeKey);
  }

  // JWT 생성 메서드
  // valueMap : 사용자 정보, min: 토큰 유효시간 -> claims로 바뀜
  // new HashMqp<>(claims)을 사용하면, 새로운 객체를 생성하여 원본 claims와 분리되므로 예외를 방지할 수 있음.
  public String generateToken(Map<String, Object> valueMap, int min) {

    try {
      // HMAC-SHA 알고리즘을 이용하여 비밀키 생성 -> UTF-8형식으로 변환
//      key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
      

      // 토큰 객체 생성
      String jwtStr = Jwts.builder() // JWT API를 통해 가져온 Jwts 객체
              .setHeader(Map.of("typ","JWT")) // 헤더 설정
//              .setClaims(valueMap) // 사용자 정보 추가
              .setClaims(valueMap) // 사용자 정보 추가 위보다 안정적
              .setIssuedAt(Date.from(ZonedDateTime.now().toInstant())) // 발급시간
              .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant())) // 만료시간
              .signWith(key) // ✅ SecretKey 사용
              .compact();
      return jwtStr;
    }catch (Exception e){
      throw new RuntimeException(e.getMessage());
    }



  }

  // 토큰 유효성 검사 (검증 메서드)
  // 파싱 : 데이터를 분석하고 분해해서 원하는 형태로 변환하는 과정
  // 반환형 : Claims로 변경 (JWT 라이브러리는 Claims 객체를 반환함)
  public Map<String, Object> validateToken(String token) {

//    Map<String, Object> claim = null;
    
    try {

      // HMAC-SHA 알고리즘을 이용하여 비밀키 생성 -> UTF-8형식으로 변환
//      SecretKey key =Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

      return  Jwts.parserBuilder() // 분해 해서 분석
              .setSigningKey(key) // 서명 검증 SecretKey 직접 사용
              .build()
              .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
              .getBody();
    } catch (MalformedJwtException e) { // jwt 형식이 올바르지 않음
      throw new CustomJWTException("MalFormed") ;
    } catch (ExpiredJwtException e){ // 토큰이 만료됨
      throw new CustomJWTException("Expired") ;
    } catch (InvalidClaimException e){ // 클레임 정보가 올바르지 않음
      throw new CustomJWTException("Invalid") ;
    } catch (JwtException e){ // 기타 jwt 관련 예외
      throw new CustomJWTException("JWTError") ;
    } catch (Exception e){ // 기타 알수 없는 예외
      throw new CustomJWTException("Error") ;
    }

  }

}
