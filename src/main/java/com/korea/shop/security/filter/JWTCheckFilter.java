package com.korea.shop.security.filter;

import com.google.gson.Gson;
import com.korea.shop.domain.Address;
import com.korea.shop.dto.MemberDTO;
import com.korea.shop.security.MemberDetails;
import com.korea.shop.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        log.info("==========<shouldNotFilter>==========");

        String path = request.getServletPath();

        // 로그인 요청, /api/items, OPTIONS는 필터 제외 - 보안검색
        if (path.startsWith("/api/members/login") || path.startsWith("/api/items/")){
            return true;
        }

        /*
        * 브라우저 -> 서버 요청 보내기 전에 "이 서버와 안전하게 대화 가능?" 먼저 확인함
        * 이런 확인 요청을 OPTIONS이라고 함 = preflight
        * 보안 떄문에, A브라우저 -> B로 요청 보낼때 내 요청 수락 가능?
        * PUT, DELETE 수정, 삭제 요청할경우 사전에 보냄 = 사전 체크하는 기능
        */
        if (request.getMethod().equals("OPTIONS")){
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("=====================< JWTCheckFilter >=============================");

        String authHeaderStr = request.getHeader("Authorization");

        if (authHeaderStr == null || !authHeaderStr.startsWith("Bearer ")){
            log.info("==================< NO JWT Found, skipping Filter >============================");
            filterChain.doFilter(request,response);
            return;
        }
        
        try {
            // Bearer 타입 accesstoken 실제 토큰 부분 추출
            // 토큰추출 -> 사용자 정보 추출 -> MemberDetails 변환 -> 인증 객체 생성 -> 저장
            String accessToken = authHeaderStr.substring(7);
            
            // JWT 검증 및 claims 추출
            // 사용자 정보 추출
            Map<String, Object> claims = jwtUtil.validateToken(accessToken); //통과되지 않았으면 예외발생

            String email = (String) claims.get("email");
            String name = (String) claims.get("name");
            String pw = (String) claims.get("pw");
            Address address = (Address) claims.get("address");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            // Spring Security 권한 변환
            List<GrantedAuthority> authorities = roleNames.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                    .collect(Collectors.toList());

            // memberDetails 객체 생성
            MemberDetails memberDetails = new MemberDetails(email ,pw ,name ,address ,authorities);

            log.info("----------Authenticated User: " + memberDetails.getUsername() + "-----------");
            log.info("----------User Roles: " + authorities + "-----------");

            // Authentication 객체를 생성하여 SecurityContext에 저장 (중요)
            /*
            * Spring Security 인증 객체 설정 - 사용자를 수동으로 인증 시키는 역할
            * 객체의 값으로 설정 (사용자 정보 dto, 비밀번호, 사용자 권한)
            */
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(memberDetails, null, authorities);

            /*
            * 시큐리티 컨텍스 객체 - 인증된 사용자 정보 저장하는 공간
            * 접근할 때 SecurityContextHolder.getContext()를 통해서 접근한다
            * 토큰으로 인증
            */
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request,response);// 통과되면 이게 실행됨

        } catch (Exception e) {
            
            log.error("JWT Check Error : "+e.getMessage());

            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("Error", "ERROR_ACCESS_TOKEN"));
            // access_token의 유효시간이 만료되면 403에러발생
            
            response.setContentType("application/json");
            // Json 데이터 출력 - 인증된 사용자 정보가 Json형태로 반환
            PrintWriter printWriter = response.getWriter(); // HTTP응답을 출력할 PrintWriter 객체 생성
            printWriter.println(msg); // 클라이언트에게 반환
            printWriter.close(); // 출력 닫음, 토큰 반환
        }

    }
}
