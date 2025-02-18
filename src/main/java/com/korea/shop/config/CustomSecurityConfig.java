package com.korea.shop.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        // 1) cors 설정
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(configurationSource());
        });

        // 2) 세션 사용 - 안함
        http.sessionManagement(sessionConfig ->
                sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 3) csrf 설정 비활성활
        http.csrf( config-> config.disable());

        // 4) 로그인 방식, 페이지 설정
        http.formLogin( config -> {
            config.loginPage("/api/member/login"); // 로그인 페이지 - 서버 경로
        }); 

        // http.authorizeHttpRequests(); // url 패턴별 인증,권한 설정


        // http.httpBasic(); // HTTP Basic 인증 (Authorization 헤더에 Basic base64(ID:PW))
        // http.oauth2Login(); // 소셜 로그인 방식

        // http.logout();
        
        return http.build();
    }

    // cors 설정 내용
    @Bean
    public CorsConfigurationSource configurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // originPattersn는 - 개발환경, 실제 리액트 배포 url을 작성한다.
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000")); // 리액트 서버
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true); //  쿠키 인증 포함 정보

        // cors 설정 적용한 url patter 지정하는 객체 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 서버 내 모든 경로에 설정 적용
        return source;
    }

    // 패스워드 인코더
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
/*
    SecurityFilterChain
    - 보안게이트(경비시스템)
    - 어떤 사람이 들어올 때, 어떤 게이트 거쳐야 하는지 결정
    - 일반 방문자 1번 게이트, 직원 2번 게이트, VIP - 3번 게이트
    - 각각 게이트에 다른 보안 규칙이 적용될 수 있다.
    * 스프링 시큐리티에서 요청이 들어올 때, 어떤 보안 필터를 적용할지 정의하는 필터 체인
      = 요청하는 url에 따라 각각 다른 패턴 적용 가능

    HttpSecurity
    - 보안 규칙을 정하는 경비원과 같다.
    - 주민이세요? (주민 자격 확인), 외부 손님? (허락 받았어요?), 신분증, 허락증 검사..... 보안
    - SecurityFilterChain 내부에서 사용, 보안 관련 세부 설정,
    - 요청 인증, 권한 부여, CSRF, 세션 정책, 로그인/로그아웃, jwt 설정등...

 */