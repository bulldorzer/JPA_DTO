package com.korea.shop.service;

import com.korea.shop.domain.Address;
import com.korea.shop.domain.Member;
import com.korea.shop.domain.MemberRole;
import com.korea.shop.dto.MemberDTO;
import com.korea.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Long saveMember(MemberDTO memberDTO) {
        validateDuplicateMember(memberDTO); // 중복 회원 검증 로직 실행
        Member member = Member.builder()
                .email(memberDTO.getEmail())
                .pw(memberDTO.getPw())
                .name(memberDTO.getName())
                .address(memberDTO.getAddress())
                .memberRoleList(memberDTO.getRoleNames().stream()
                        .map(MemberRole::valueOf)// String -> Enum으로 변환
                        .collect(Collectors.toList())).build();
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(MemberDTO memberDTO) {
        List<Member> foundMember = memberRepository.findByName(memberDTO.getName());
        if (!foundMember.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 회원");
        }
    }

    @Override
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(member -> modelMapper.map(member, MemberDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public MemberDTO getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return modelMapper.map(member, MemberDTO.class);
    }

    @Transactional
    @Override
    public void deleteMember(Long id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
        } else {
            throw new RuntimeException("Member not found");
        }
    }
/*
{
    "name" : "john",
    "address" : {
        "city" : "서울",
        "zipcode" : "12345"
    }
}
* */
    @Transactional
    @Override
    public void updateMember(Long id, MemberDTO memberDTO) {
        // 엔티티 조회
        Member searchMember = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        searchMember.changeName(memberDTO.getName()); // dto값 꺼내서 엔티티에 설정
        searchMember.changeEmail(memberDTO.getEmail());

        if (memberDTO.getAddress() != null) { // address 있으면

            Address address = modelMapper.map(memberDTO.getAddress(), Address.class);

            searchMember.getAddress().setCity(address.getCity());
            searchMember.getAddress().setStreet(address.getStreet());
            searchMember.getAddress().setZipcode(address.getZipcode());
        }
        // 변경 감지(Dirty Checking)하여 save 생략 가능 (알아서 update 쿼리 실행됨)
        // 전제 : 영속성 컨텍스트에 의해 관리되는 객체만 처리 - 만약 아니라면 save or merge 해야함
    }

    @Override
    public List<MemberDTO> getMembersByName(String name) {
        return memberRepository.findByName(name).stream()
                .map(member -> modelMapper.map(member, MemberDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return memberRepository.existsById(id);
    }
}
