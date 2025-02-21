package com.korea.shop.domain;

public enum MemberRole {
    USER, // 사용자 권한
    MANAGER, // 매니저 권한
    ADMIN; // 관리자 권한
}

/*
    public class MemberRole {
        static final int USER = 0;
        static final int MANAGER = 1;
        static final int ADMIN = 2;
    }
    위코딩과 목적성은 같음
    단 위 코딩은 클래스 기반으로 단순한 상수, switch문에서 사용불과, 문자열 반환이나 관련 메서드 추가X

    public enum MemberRole { USER, MANAGER, ADMIN }
    Enum(열거형) 사용한 역할
    - USER, MANAGER, ADMIN -- 고유한 인스턴스(객체)
    - toString(), ordinal()등의 기본 메서드가 제공
    - 추가적인 필드, 메서드 포함 가능
    - value() : 상수를 배열로 반환 [ USER, MANAGER, ADMIN ]
    - valueOf("인스턴스이름") : 문자열 -> enum 상수로 변환
    ex) MemberRole.valueOf("MANAGER") = 1
    
    - ordinal() : 열거형 상수의 인덱스(순서)를 반환 0부터 시작
    ex) MemberRole.USER.ordinal() = 0 출력
    ex) MemberRole.MANAGER.ordinal() = 1 출력
    
    // 기능은 같음
    - toString() : enum 상수 이름을 - 문자열로 변환 - 오버이딩 가능 (기능 재정의) - name과 다른점
    - name() : enum 상수이름을 문자열로 변환

*/