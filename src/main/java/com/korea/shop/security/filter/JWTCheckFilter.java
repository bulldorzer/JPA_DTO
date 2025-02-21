package com.korea.shop.security.filter;

import com.google.gson.Gson;
import com.korea.shop.domain.Address;
import com.korea.shop.dto.MemberDTO;
import com.korea.shop.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        //Preflignt요청은 체크하지 않음
        if (request.getMethod().equals("OPTIONS")){
            return true;
        }

        String path = request.getRequestURI();

        log.info("check URI ... "+path);

        // api/members/ 경로의 호출은 체크하지 않음
        if (path.startsWith("/api/members/login")){
            return true;
        }

        if (path.startsWith("/api/items/")){
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
            String accessToken = authHeaderStr.substring(7);
            
            //JWT 검증 및 claims 추출
            Map<String, Object> claims = JWTUtil.validateToken(accessToken); //통과되지 않았으면 예외발생



            String email = (String) claims.get("email");
            String name = (String) claims.get("name");
            String pw = (String) claims.get("pw");
            Address address = (Address) claims.get("address");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            MemberDTO memberDTO = new MemberDTO(email ,pw ,name ,address ,roleNames);

            log.info("============<MemberDTO>==============");
            log.info(memberDTO);
            log.info(memberDTO.getAuthorities());

            /*
            * Spring Security 인증 객체 설정 - 사용자를 수동으로 인증 시키는 역할
            * 객체의 값으로 설정 (사용자 정보 dto, 비밀번호, 사용자 권한)
            */
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(memberDTO, pw, memberDTO.getAuthorities());

            /*
            * 시큐리티 컨텍스 객체 - 인증된 사용자 정보 저장하는 공간
            * 접근할 때 SecurityContextHolder.getContext()를 통해서 접근한다
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
