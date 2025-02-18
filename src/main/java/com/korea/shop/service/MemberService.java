package com.korea.shop.service;

import com.korea.shop.domain.Member;
import com.korea.shop.dto.MemberDTO;

import java.util.List;

public interface MemberService {
    Long saveMember(MemberDTO memberDTO);
    List<MemberDTO> getAllMembers();
    MemberDTO getMember(Long id);
    void deleteMember(Long id);
    void updateMember(Long id, MemberDTO memberDTO);
    List<MemberDTO> getMembersByName(String name);
    boolean existsById(Long id);
}
