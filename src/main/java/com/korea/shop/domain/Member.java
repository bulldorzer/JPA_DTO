package com.korea.shop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder // 객체생성+초기화
@ToString(exclude = "memberRoleList")// ToString 메서드에서 memberRoleList 제외
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 일련번호 붙혀줌 auto_increment
    @Column(name="member_id") // 컬럼명 변경
    private Long id;

    private String name;
    private String email;
    private String pw;

    @Embedded // 값 타입 포함
    private Address address;

    //    @OneToMany(mappedBy = "member") // 연결관계 - 거울 설정 (양방향)
    //    private List<Order> orders = new ArrayList<>();
    @ElementCollection(fetch = FetchType.LAZY)// 지연로딩, 필요시 추가 데이터 생성
    @Builder.Default // 빌더를 사용할 때, 필드의 기본값 유지, 안하면 null 초기화함.
    private List<MemberRole> memberRoleList = new ArrayList<>();

    public void addRole(MemberRole memberRole){
        memberRoleList.add(memberRole);
    }

    public void clearRole(){
        memberRoleList.clear();
    }

    public void changeName(String name){
        this.name = name;
    }
    public void changePw(String pw){
        this.pw = pw;
    }

    public void changeEmail(String email){
        this.email = email;
    }

}
