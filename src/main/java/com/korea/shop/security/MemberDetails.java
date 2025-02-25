package com.korea.shop.security;

import com.korea.shop.domain.Address;
import com.korea.shop.domain.Member;
import com.korea.shop.dto.MemberDTO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/*
* 사용자의 정보를 담는 클래스
*/
@Getter
public class MemberDetails implements UserDetails {

    private final String email;
    private final String password;
    private final String name;
    private final Address address;
    private final List<GrantedAuthority> authorities;

    // GrantedAuthority 형태로 저장할때 권한에 반드시 "ROLE_" 글자가 있어야함
    // USER-> ROLE_USER 형태로 저장되어 있어야함
    // GrantedAuthority : 인터페이스 -> SimpleGrantedAuthority : 구현 클래스
    // 사용자의 권한을 저장하는 인터페이스와 구현 클래스이다
    public MemberDetails(Member member){
        this.email = member.getEmail();
        this.password = member.getPw();
        this.address = member.getAddress();
        this.name = member.getName();
        // SimpleGrantedAuthority 사용자 권한을 문자열로 저장하는 클래스
        this.authorities = member.getMemberRoleList().stream()
                .map(role-> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toList());
    }

    public MemberDetails(String email, String password, String name, Address address, List<GrantedAuthority> authorities) {
        // 아래 3가지는 기본 이전 USER클래스에서 필요했던 매개변수
        this.email = email; // 이메일
        this.password = password; // 비밀번호
        this.authorities = authorities; // 권한목록
        
        // 필요에 따라 추가 
        this.name = name;
        this.address = address;
    }

    // MemberDetails -> MemberDTO로 변환하는 메서드
    /*
    *  GrantedAuthority 데이터 형태는 ROLE_USER, ROLE_ADMIN
    */
    public MemberDTO toMemberDTO() {
        return new MemberDTO(this.email, this.password, this.name, this.address,
                this.authorities.stream()
                        .map(GrantedAuthority::getAuthority)// 권한을 문자열로 가져오기
                        .map(role -> role.replace("ROLE_",""))// ROLE_ 글자제거
                        .collect(Collectors.toList())// List타입으로 저장
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired(){ return true; }

    @Override
    public boolean isAccountNonLocked(){return true;}

    @Override
    public boolean isCredentialsNonExpired(){ return true; }

    @Override
    public boolean isEnabled(){ return true; }

}
