package com.korea.shop.config;

import com.korea.shop.domain.Member;
import com.korea.shop.dto.MemberDTO;
import org.modelmapper.ModelMapper;

import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Configuration
public class RootConfig {
    @Bean
    public ModelMapper modelMapper() {
        
        // 도구만들기
        ModelMapper modelMapper = new ModelMapper();

        // 옵션 셋팅
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) // 필드 이름이 동일한 경우 자동 매핑을 허용
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) // 필드 접근 수준을 private까지 허용합니다.
                .setMatchingStrategy(MatchingStrategies.STRICT);
        // LOOSE 이름 정확하지 않아도 알아서 매핑
        // STRICT 이름 정확하게

        // ✅ Member -> MemberDTO 변환 설정 (roleNames 변환 추가)
        TypeMap<Member, MemberDTO> typeMap = modelMapper.createTypeMap(Member.class, MemberDTO.class);

        // 매핑 - 사용자 설정(수동)
        typeMap.addMappings(mapper -> {
            // Member엔티티의 필드값을 MemberDTO 필드에 정의
            mapper.map(Member::getEmail, MemberDTO::setUsername); // memberDTO.setUseremail( member.getEmail());
            mapper.map(Member::getPw, MemberDTO::setPassword);
            mapper.map(src -> (
                            src.getMemberRoleList() == null
                                    ? new ArrayList<>()
                                    : src.getMemberRoleList().stream()
                                    .map(Enum::name)// Enum객체를 문자열로 변환합니다.
                                    .collect(Collectors.toList())),
                    MemberDTO::setRoleNames);
        });

        // 내보내기
        return modelMapper;
    }
}
