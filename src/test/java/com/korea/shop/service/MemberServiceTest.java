package com.korea.shop.service;

import com.korea.shop.domain.MemberRole;
import com.korea.shop.dto.MemberDTO;
import com.korea.shop.domain.Address;
import com.korea.shop.domain.Member;
import com.korea.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) // JUnit5 기반 테스트
@SpringBootTest
// @Transactional(readOnly = true)
public class MemberServiceTest {

    /*
    * 1) ModelMapper객체화
    * 2) MemberService 구현화
    * 3) MemberRepository 객체화
    * 4) EntityManager 객체화
    * 5) PasswordEncoder 객체화
    * */
    @Autowired private ModelMapper modelMapper;
    @Autowired private MemberService memberService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private EntityManager em;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String[] DISTRICTS = {
            "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구",
            "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구",
            "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"
    };

    @Test
        // @Transactional
    /*
    ✅✅✅ 중시! 항상 테스트 파일에서는 @Test어노테이션이 필수!!!!
    * 1) 랜덤객체생성
    * 2) 10개의 사용자를 만들기 위해 10번 반복
    * 3) 각 엔티티 필드에 값의 초기화
    * 4) 비밀번호는 암호화 처리
    * 5) 반복문 한번마다 한행의 객체 생성 builder pattern ( builder().build(); )
    * 6) 1~4 User권한 5~7 MANAGER 권한 8~10 ADMIN권한 부여
    * 7) Service에서는 매개변수를 DTO형태로 받기 떄문에 mapper로 DTO변환
    * 8) 멤버 저장 실행
    * */
    void 멤버_더미데이터_생성() {
        Random random = new Random();
        IntStream.range(0,10).forEach(i->{
            String name = "KimSeulGi"+i;
            String email = "KimSeulGi"+i+"@Redvelet.com";
            String pw = passwordEncoder.encode("3333");
            String street = DISTRICTS[random.nextInt(DISTRICTS.length)];
            String zipcode = String.valueOf(10000+ random.nextInt(90000));
            Address address = new Address("서울",street,zipcode);
            Member member = Member.builder()
                    .name(name).email(email).pw(pw).address(address).build();
            member.addRole(MemberRole.USER);
            if (i>4){
                member.addRole(MemberRole.MANAGER);
            }
            if (i>7){
                member.addRole(MemberRole.ADMIN);
            }
            MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);
            memberService.saveMember(memberDTO);
        });

        long count = memberService.getAllMembers().size();
        assertThat(count).isEqualTo(10); // 10과 같은지 아니면 false 생성
    }

    @Test
    public void 회원가입_테스트() throws Exception {
        // ✅ given - 테스트 데이터 생성

        Address address = new Address("서울","마포구","88888");
        Member member = Member.builder()
                .address(address)
                .name("CHOI")
                .email("user5542@aaa.com")
                .pw(passwordEncoder.encode("1111"))
                .build();
        member.addRole(MemberRole.USER);

        MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);

        // ✅ when - 회원 가입 로직 실행
        Long savedId = memberService.saveMember(memberDTO);

        // ✅ then - 결과 검증
        Member savedMember = memberRepository.findById(savedId).orElseThrow();

        assertEquals(member.getName(), savedMember.getName(), "이름이 일치해야 합니다.");
        assertEquals(member.getEmail(), savedMember.getEmail(), "이메일이 일치해야 합니다.");
        assertEquals(member.getPw(), savedMember.getPw(), "비밀번호가 일치해야 합니다.");

        System.out.println("✅ 회원가입 테스트 성공! 저장된 ID: " + savedId);
    }

    @Test
    /*
    * 1) 총 2개의 중복회원 객체 생성
    * 2) 첫 번째 회원 멤버저장 후 메세지 표시
    * 3) 두 번째 회원 멤버 저장시 예외 처리 확인*/
    public void 중복회원_예외_테스트() {
        Random random = new Random();
        String street = DISTRICTS[random.nextInt(DISTRICTS.length)];
        String zipcode = String.valueOf(10000+ random.nextInt(90000));
        Address address = new Address("서울시",street,zipcode);
        Member mem1 = Member.builder()
                .name("ZZZ").email("zzz@zzz.com").pw("1111").address(address).build();
        mem1.addRole(MemberRole.USER);
        MemberDTO mem1DTO = modelMapper.map(mem1, MemberDTO.class);
        Member mem2 = Member.builder()
                .name("ZZZ").email("zzz@zzz.com").pw("1111").address(address).build();
        mem2.addRole(MemberRole.USER);

        Long result = memberService.saveMember(mem1DTO);
        System.out.println("mem1DTO 회원가입 성공"+ result);

        System.out.println("--- 중복체크 확인 ---");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->{
            memberService.saveMember(modelMapper.map(mem2, MemberDTO.class));
        });

        assertEquals("이미 존재하는 회원",exception.getMessage(),"중복 메세지가 정확해야합니다!");
        System.out.println(exception.getMessage());
    }

    @Test
    public void 회원조회_테스트() {
        // ✅ given - 회원 생성 및 저장
        // ✅ when - ID로 회원 조회
        // ✅ then - 회원 정보 검증
        Member member = Member.builder()
                .name("EEE").pw(passwordEncoder.encode("1111")).build();
        Long id = memberService.saveMember(modelMapper.map(member,MemberDTO.class));

        MemberDTO foundmem = memberService.getMember(id);

        assertNotNull(foundmem,"회원이 정상적으로 조회되어야합니다");
        assertEquals(member.getName(),foundmem.getName(),"이름이 일치해야합니다.");
        assertEquals(member.getEmail(),foundmem.getEmail(),"이메일 일치해야합니다.");

        System.out.println("멤버 조회 성공 "+id);

    }

    // ✅ 존재하지 않는 회원 조회 시 예외 발생 테스트
    @Test
    public void 존재하지_않는_회원조회_예외_테스트() {
        // ✅ when & then - 존재하지 않는 회원 조회 시 예외 발생 검증
        RuntimeException exception = assertThrows(RuntimeException.class,()->{
            memberService.getMember(999L);
        });

        assertEquals("Member not found",exception.getMessage(),"올바른 예외값이 아닙니다");
        System.out.println(exception.getMessage());

    }

    @Test
    public void 회원삭제_테스트() {
        // ✅ given - 회원 생성 및 저장
        // ✅ when - 회원 삭제
        // ✅ then - 회원이 실제로 삭제되었는지 확인
        Member member = Member.builder().name("유세윤").pw("1111").build();

        Long id = memberService.saveMember(modelMapper.map(member,MemberDTO.class));

        memberService.deleteMember(id);

        assertFalse(memberService.existsById(id),"삭제된 회원은 조회되지 않아야 합니다");
        System.out.println(id+" 삭제완료.");
    }

    // ✅ 존재하지 않는 회원 삭제 시 예외 발생 테스트
    @Test
    public void 존재하지_않는_회원삭제_예외_테스트() {
        // ✅ when & then - 존재하지 않는 회원 삭제 시 예외 발생 검증
        RuntimeException exception = assertThrows(RuntimeException.class,()->{
            memberService.deleteMember(999L);
        });

        assertEquals("Member not found",exception.getMessage(),"예외가 메세지가 다르게 나옴");
        System.out.println(exception.getMessage());
    }

    @Test
    @Transactional
    public void 회원정보_업데이트_테스트() {
        // ✅ given - 회원 생성 및 저장
        // ✅ DB 반영을 강제하여 memberRoleList 저장 확인
        // ✅ when - 회원 정보 수정
        // ✅ ModelMapper를 사용해서 변환 (수정된 설정 반영됨)
        // ✅ then - 회원 정보가 정상적으로 변경되었는지 검증
        Member member = Member.builder()
                .name("이혜지").pw("1111")
                .address(new Address("서울시","광진구","88373")).build();
        member.addRole(MemberRole.USER);
        Long memberId = memberService.saveMember(modelMapper.map(member,MemberDTO.class));

        System.out.println("바뀌기전"+memberService.getMember(memberId).getName());

        em.flush();
        em.clear();

        Member updateMember = Member.builder()
                .name("김슬기").pw("1111")
                .address(new Address("전라남도","광주시","11541")).build();

        memberService.updateMember(memberId,modelMapper.map(updateMember,MemberDTO.class));
        System.out.println("바뀐 후"+memberService.getMember(memberId).getName());

        MemberDTO updatestatus = memberService.getMember(memberId);

        assertEquals("김슬기",updatestatus.getName(),"이름 변경이 되어야함");
        assertEquals("11541",updatestatus.getAddress().getZipcode(),"집코드가 변경되아야함");
        assertTrue(updatestatus.getRoleNames().contains("USER"), "권한이 USER로 되어있어야함");

        System.out.println("updatestatus => "+updatestatus);

    }


    // ✅ 존재하지 않는 회원 업데이트 시 예외 발생 테스트
    @Test
    public void 존재하지_않는_회원_업데이트_예외_테스트() {
        // ✅ given - 존재하지 않는 ID
        // 수정시킬 멤버 생성 멤버저장
        // ✅ when & then - 존재하지 않는 회원 업데이트 시 예외 발생 검증
        Long nonExistId = 99999L;

        RuntimeException exception = assertThrows(RuntimeException.class, ()->{
            Member member = Member.builder()
                    .name("실험용쥐").pw(passwordEncoder.encode("1111")).address(new Address("서울시","마포구","54236")).build();

            Long memberId = memberService.saveMember(modelMapper.map(member,MemberDTO.class));
            MemberDTO updateMember = memberService.getMember(memberId);

            memberService.updateMember(nonExistId,updateMember);
        });

        assertEquals("Member not found",exception.getMessage(),"올바른 예외 메세지가 아닙니다");
        System.out.println("에러메세지: "+exception.getMessage());
    }



}

