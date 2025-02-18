package com.korea.shop.controller;

import com.korea.shop.dto.MemberDTO;
import com.korea.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 모든 회원 조회
    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    // 특정 회원 조회 (ID 기준)
    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMember(id));
    }

    // 회원 등록
    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO memberDTO) {
        Long memberId = memberService.saveMember(memberDTO);
        return ResponseEntity.ok(memberDTO);
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    // 회원 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO) {
        memberService.updateMember(id, memberDTO);
        return ResponseEntity.ok(memberService.getMember(id));
    }

    // 특정 이름으로 회원 검색
    @GetMapping("/search")
    public ResponseEntity<List<MemberDTO>> getMembersByName(@RequestParam String name) {
        return ResponseEntity.ok(memberService.getMembersByName(name));
    }

    // 회원 존재 여부 확인
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.existsById(id));
    }
}
