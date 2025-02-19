package com.korea.shop.dto;

import com.korea.shop.domain.Address;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

@Getter@Setter
@ToString(exclude = "pw")
public class MemberDTO extends User {
    private Long id;
    private String name;
    private String email; // 로그인 아이디의 역할
    private String pw; // 비밀번호
    private Address address;
    
    private List<String> roleNames = new ArrayList<>(); // 권한 리스트 엔티티와 다름
    
    public MemberDTO(String email, String pw, String name, Address address, List<String> roleNames){
        // user클래스 생성할때 (로그인아이디, 비밀번호, 역할( ROLE_ 글자가 있어야 함 ))
        super(
                email,
                pw,
                roleNames.stream().map(str->new
                        SimpleGrantedAuthority("ROLE_"+str)).collect(Collectors.toList())

        );

        this.email = email;
//        this.pw = pw;
        this.name = name;
        this.address = address;
        this.roleNames = roleNames;


    }

    // DTO 데이터 -> MAP 형태로 사용할 수 있도록 변환하는 메서드
    public Map<String,Object> getClaims(){

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("email", email);
        dataMap.put("pw", pw); // 추후제거 (pw 공개안함)
        dataMap.put("name", name);
        dataMap.put("roleNames", roleNames);

        return dataMap;
    }

}
