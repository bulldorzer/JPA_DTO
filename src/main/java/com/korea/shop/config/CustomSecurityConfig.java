package com.korea.shop.config;

import com.korea.shop.security.filter.JWTCheckFilter;
import com.korea.shop.security.handler.APILoginFailHandler;
import com.korea.shop.security.handler.APILoginSuccessHandler;
import com.korea.shop.security.handler.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
            /*config.loginPage("/api/members/login"); // 로그인 페이지 - 서버 경로
            config.loginProcessingUrl("/api/member/login") // 로그인 페이지 - 서버 경로 - post방식
            config.usernameParameter("username").passwordParameter("password");
            config.successHandler(new APILoginSuccessHandler()); // 로그인 성공 처리
            config.failureHandler(new APILoginFailHandler()); // 로그인 실패 처리*/
            
            
            config.loginPage("/api/members/login") // 로그인 페이지 - 서버 경로
            .loginProcessingUrl("/api/members/login") // 로그인 페이지 - 서버 경로 - post방식
            .usernameParameter("username").passwordParameter("password") // 파라미터명 이름 설정
            .successHandler(new APILoginSuccessHandler()) // 로그인 성공 처리
            .failureHandler(new APILoginFailHandler()); // 로그인 실패 처리
        }); // 일반적은 form로

        // http.authorizeHttpRequests(); // url 패턴별 인증,권한 설정


        // http.httpBasic(); // HTTP Basic 인증 (Authorization 헤더에 Basic base64(ID:PW))
        // http.oauth2Login(); // 소셜 로그인 방식

        // http.logout();
        
        

        // 5) 접근거부 예외처리 핸들러 등록
        http.exceptionHandling(config ->{
           config.accessDeniedHandler(new CustomAccessDeniedHandler());
        });
        
        /*// 6) 로그인 요청 모두 허용
        http.authorizeHttpRequests(auth ->{
            auth.requestMatchers("/api/members/login").permitAll(); // 로그인 요청 허용
            auth.anyRequest().authenticated(); // 다른 요청은 인증필요
        });*/

        // 6) JWT 인증 필터 추가 - UsernamePasswordAuthenticationFilter 실행전에 JWTCheckFilter가 실행됨
        http.addFilterBefore(new JWTCheckFilter(),
                UsernamePasswordAuthenticationFilter.class);// JWT 체크
        
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
