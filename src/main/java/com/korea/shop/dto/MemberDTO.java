package com.korea.shop.dto;

import com.korea.shop.domain.Address;
import com.korea.shop.domain.Member;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

@Getter@Setter
//@ToString(exclude = "pw")
@AllArgsConstructor
@NoArgsConstructor
//public class MemberDTO extends User {
public class MemberDTO {
    private Long id;
    private String name;
    private String email; // 로그인 아이디의 역할
    private String pw; // 비밀번호
    private Address address;
    
    private List<String> roleNames = new ArrayList<>(); // 권한 리스트 엔티티와 다름
    
    public MemberDTO(Member member){
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.pw = member.getPw();
        this.address = member.getAddress();
        this.roleNames = (member.getMemberRoleList()==null) 
                                ? new ArrayList<>() 
                                : member.getMemberRoleList().stream()
                                .map(Enum::name)// Enum타입을 String 타입으로 변환
                                .collect(Collectors.toList());
    }

    public MemberDTO(String email, String pw, String name, Address address, List<String> roleNames){
        this.email = email;
        this.pw = pw;
        this.name = name;
        this.address = address;
        this.roleNames = roleNames;
    }

    // DTO 데이터 -> MAP 형태로 사용할 수 있도록 변환하는 메서드, 토큰 변환에 필요함 DTO -> Map -> JSON 형식
    public Map<String,Object> getClaims(){

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("email", email);
        dataMap.put("pw", pw); // 추후제거 (pw 공개안함)
        dataMap.put("name", name);
        dataMap.put("roleNames", roleNames);

        return dataMap;
    }

//    // ✅ Security UserDetails 필드를 매핑하기 위한 setter 추가
//    public void setUsername(String username){
//        this.email = username; // 'User' 의 username 매핑
//    }
//
//    public void setPassword(String password){
//        this.pw = password; // 'User' 의 password 매핑
//    }

}
