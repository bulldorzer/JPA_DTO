package com.korea.shop.config;

import com.korea.shop.security.filter.JWTCheckFilter;
import com.korea.shop.security.handler.APILoginFailHandler;
import com.korea.shop.security.handler.APILoginSuccessHandler;
import com.korea.shop.security.handler.CustomAccessDeniedHandler;
import com.korea.shop.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Log4j2
@EnableMethodSecurity
public class CustomSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private final JWTUtil jwtUtil;
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{



        log.info("===================<Security Config>=======================");

        // 메서드 체인 으로 연결법
        // http.메서드().메서드().메서드(); 메서드 체인 이라고함 => 메서드로 이어지는 코딩

        // CORS 설정: 다른 도메인에서 요청 가능하도록 설정
        http.cors(
                        httpSecurityCorsConfigurer
                                -> httpSecurityCorsConfigurer.configurationSource(configurationSource()))
                // 세션 사용 X
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // url 접근 제어 허용 가능한 url 등록
                .authorizeHttpRequests( atuhz -> {
                    atuhz
                            .requestMatchers("/api/members/login").permitAll() // 로그인 화면
                            .requestMatchers("/api/orders").hasRole("USER") //  주문목록
                            .requestMatchers("/api/deliveries").hasRole("USER") //  상품목록
                            .anyRequest().authenticated(); // 다른 요청은 인증 필요
                })
                // JWT 필터 설정 ( 로그인 필터 )
                .addFilterBefore(new JWTCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)// 여기가 순서 제일 중요
                // cors -> csrf 비활성화 -> 세션 사용x (반드신 필터 설정 전에 와야 함)
                .csrf( config -> config.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                // 로그인 설정(폼설정)
                .formLogin(form -> form.disable())
                // 예외처리 핸들러 ( 필터 적용 후 설정)
                .exceptionHandling(config -> config.accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();
    }

    // cors 설정 내용
    @Bean
    public CorsConfigurationSource configurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // orginPatterns은 개발환경, 실제 리액트 배포 url을 작성한다(=허용할 도메인,출저를 작성)
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000")); // 리액트 서버요청만 허용
        // 도메인에서 허용할 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        // 허용할 헤더 내용
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        //  자격 증명 허용
        configuration.setAllowCredentials(true); 

        // cors 설정 적용한 url pattern 지정하는 객체 생성
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
