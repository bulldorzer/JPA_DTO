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
        log.info(username);
        if (member == null){
            log.error("User not Found: "+ username);
            throw new UsernameNotFoundException("User with email "+username+"Not Found"); // 명확한 메세지 제공
        }

        // MemberDTO 타입으로 반환
        MemberDTO memeberDTO = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getName(),
                member.getAddress(),
                member.getMemberRoleList().stream().map(memberRole
                        -> memberRole.name()).collect(Collectors.toList())
        );

        log.info("memeberDTO: "+memeberDTO);
        return memeberDTO;
    }
}
