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
    void 멤버_더미데이터_생성() {
        Random random = new Random();

        IntStream.range(0, 10).forEach(i -> {
            String name = "user" + i;
            String email = "user" + i + "@aaa.com";
            String password = "1111";
            String district = DISTRICTS[random.nextInt(DISTRICTS.length)];
            String zipcode = String.valueOf(10000 + random.nextInt(90000));

            Address address = new Address("서울", district, zipcode);
            Member member = Member.builder()
                    .email(email)
                    .pw(passwordEncoder.encode(password))
                    .name(name)
                    .address(address)
                    .build();

            member.addRole(MemberRole.USER);
            if(i>=5){
                member.addRole(MemberRole.MANAGER);
            }
            if(i>=8){
                member.addRole(MemberRole.ADMIN);
            }

            MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);

            memberService.saveMember( memberDTO );
        });

        long count = memberService.getAllMembers().size();
        assertThat(count).isEqualTo(10);
    }

    @Test
    public void 회원가입_테스트() throws Exception {
        // ✅ given - 테스트 데이터 생성

        Address address = new Address("서울","강남구","88888");
        Member member = Member.builder()
                .address(address)
                .name("Lee")
                .email("user200@aaa.com")
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
    public void 중복회원_예외_테스트() {
        // ✅ given - 동일한 이름의 회원 2명 생성
        Member mem1 = Member.builder()
                .name("Choi")
                .pw(passwordEncoder.encode("1111"))
                .memberRoleList(List.of(MemberRole.USER))
                .build();

        Member mem2 = Member.builder()
                .name("Choi")
                .pw(passwordEncoder.encode("1111"))
                .build();

        // ✅ when & then - 중복 가입 시 예외 발생 확인
        Long savedId = memberService.saveMember(modelMapper.map(mem1, MemberDTO.class));

        System.out.println("✅ 첫 번째 회원 저장 완료! ID: " + savedId); // 🔍 로그 추가

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            memberService.saveMember(modelMapper.map(mem2, MemberDTO.class));
        });

        assertEquals("이미 존재하는 회원", exception.getMessage(), "중복 회원 예외 메시지가 정확해야 합니다.");

        System.out.println("✅ 중복 회원 예외 테스트 성공!");
    }

    @Test
    public void 회원조회_테스트() {
        // ✅ given - 회원 생성 및 저장
        Member member = Member.builder()
                .name("Hong")
                .pw(passwordEncoder.encode("1111"))
                .build();

        Long memberId = memberService.saveMember(modelMapper.map(member, MemberDTO.class));

        // ✅ when - ID로 회원 조회
        MemberDTO foundMember = memberService.getMember(memberId);

        // ✅ then - 회원 정보 검증
        assertNotNull(foundMember, "회원이 정상적으로 조회되어야 합니다.");
        assertEquals(member.getName(), foundMember.getName(), "이름이 일치해야 합니다.");
        assertEquals(member.getEmail(), foundMember.getEmail(), "이메일이 일치해야 합니다.");

        System.out.println("✅ 회원 조회 테스트 성공! ID: " + memberId);
    }

    // ✅ 존재하지 않는 회원 조회 시 예외 발생 테스트
    @Test
    public void 존재하지_않는_회원조회_예외_테스트() {
        // ✅ when & then - 존재하지 않는 회원 조회 시 예외 발생 검증
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberService.getMember(9999L); // 존재하지 않는 ID
        });

        assertEquals("Member not found", exception.getMessage(), "올바른 예외 메시지가 반환되어야 합니다.");
        System.out.println("✅ 존재하지 않는 회원 조회 예외 테스트 성공!");
    }

    @Test
    public void 회원삭제_테스트() {
        // ✅ given - 회원 생성 및 저장
        Member member = Member.builder()
                .name("홍길동")
                .pw(passwordEncoder.encode("1111"))
                .build();

        Long memberId = memberService.saveMember(modelMapper.map(member, MemberDTO.class));

        // ✅ when - 회원 삭제
        memberService.deleteMember(memberId);

        // ✅ then - 회원이 실제로 삭제되었는지 확인
        assertFalse(memberService.existsById(memberId), "삭제된 회원은 조회되지 않아야 합니다.");

        System.out.println("✅ 회원 삭제 테스트 성공! ID: " + memberId);
    }

    // ✅ 존재하지 않는 회원 삭제 시 예외 발생 테스트
    @Test
    public void 존재하지_않는_회원삭제_예외_테스트() {
        // ✅ when & then - 존재하지 않는 회원 삭제 시 예외 발생 검증
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberService.deleteMember(9999L); // 존재하지 않는 ID
        });

        assertEquals("Member not found", exception.getMessage(), "올바른 예외 메시지가 반환되어야 합니다.");
        System.out.println("✅ 존재하지 않는 회원 삭제 예외 테스트 성공!");
    }

    @Test
    @Transactional
    public void 회원정보_업데이트_테스트() {
        // ✅ given - 회원 생성 및 저장
        Member member = Member.builder()
                .name("홍길동")
                .pw(passwordEncoder.encode("1111"))
                .memberRoleList(new ArrayList<>()) // ✅ "USER" 역할 추가
                .address(new Address("부산","해운대", "98765"))
                .build();

        member.addRole(MemberRole.USER);
        MemberDTO dto = modelMapper.map(member, MemberDTO.class);
        Long memberId = memberService.saveMember(dto);

        // ✅ DB 반영을 강제하여 memberRoleList 저장 확인
        em.flush();
        em.clear();

        // ✅ when - 회원 정보 수정
        Member updateInfo = Member.builder() // ✅ Builder 사용하여 필드 유지
                .name("장보고")
                .address(new Address("서울시", "마포구", "12345"))
                .memberRoleList(new ArrayList<>(member.getMemberRoleList())) // ✅ 기존 역할 유지
                .build();

        // ✅ ModelMapper를 사용해서 변환 (수정된 설정 반영됨)
        MemberDTO updatedDto = modelMapper.map(updateInfo, MemberDTO.class);
        memberService.updateMember(memberId, updatedDto);

        // ✅ then - 회원 정보가 정상적으로 변경되었는지 검증
        MemberDTO updatedMember = memberService.getMember(memberId);

        System.out.println(updatedMember.getRoleNames().toString()); // ✅ 확인용 로그
        System.out.println(updatedMember.getRoleNames().contains("USER")); // ✅ 확인용 로그

        assertEquals("장보고", updatedMember.getName(), "이름이 변경되어야 합니다.");
        assertEquals("서울시", updatedMember.getAddress().getCity(), "주소가 변경되어야 합니다.");
        assertTrue(updatedMember.getRoleNames().contains("USER"), "회원 역할이 'USER'로 설정되어야 합니다.");

        System.out.println("✅ 회원 정보 업데이트 테스트 성공!");
    }


    // ✅ 존재하지 않는 회원 업데이트 시 예외 발생 테스트
    @Test
    public void 존재하지_않는_회원_업데이트_예외_테스트() {
        // ✅ given - 존재하지 않는 ID
        Long nonExistentId = 9999L;

        // ✅ when & then - 존재하지 않는 회원 업데이트 시 예외 발생 검증
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            Member member = Member.builder()
                    .name("새로운 회원")
                    .pw(passwordEncoder.encode("1111"))
                    .address(new Address("서울시", "마포구", "45678"))
                    .build();

            Long memberId = memberService.saveMember(modelMapper.map(member, MemberDTO.class));
            Member updateInfo = new Member();

            memberService.updateMember(nonExistentId, modelMapper.map(updateInfo, MemberDTO.class));
        });


        assertEquals("Member not found", exception.getMessage(), "올바른 예외 메시지가 반환되어야 합니다.");
        System.out.println("✅ 존재하지 않는 회원 업데이트 예외 테스트 성공!");
    }



}

