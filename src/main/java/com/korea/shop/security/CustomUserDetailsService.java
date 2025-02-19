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

import java.util.stream.Collectors;


/**
 * CustomUSerDetailsService
 * 사용자의 인증 처리를 하기 위함
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService  implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.getWithRoles(username);

        log.info("---------------<loadUserByUsername>---------------");
        if (member == null){
            throw new UsernameNotFoundException("Not Found");
        }

        // MemberDTO 타입으로 반환
        MemberDTO memeberDTO = new MemberDTO(
                member.getName(),
                member.getEmail(),
                member.getPw(),
                member.getAddress(),
                member.getMemberRoleList().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );
        return null;
    }
}
