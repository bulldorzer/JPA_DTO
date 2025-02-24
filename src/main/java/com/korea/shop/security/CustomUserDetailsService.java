package com.korea.shop.security;

import com.korea.shop.domain.Member;
import com.korea.shop.dto.MemberDTO;
import com.korea.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/*
 * UserDetailsService
 * 사용자 인증 조회 인터 페이스
 * 
 * CustomUSerDetailsService
 * 사용자의 인증 조회 구현을 하기 위함
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService  implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.getWithRoles(email)
                .orElseThrow(()-> new UsernameNotFoundException("사용자를 찾을 수 없음: "+ email)); // null이 발생할수 있으므로 예외처리

        log.info("---------------<loadUserByUsername>---------------");

//        List<String> roleNmaes = member.getMemberRoleList()
//                .stream()
//                .map(Enum::name)// String 으로 변환
//                .collect(Collectors.toList());
        
        return new MemberDetails(member);
    }
}
