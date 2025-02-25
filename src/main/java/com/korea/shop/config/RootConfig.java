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
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);


        // ✅ Member → MemberDTO 변환 설정 (roleNames 변환 추가)
        TypeMap<Member, MemberDTO> typeMap = modelMapper.createTypeMap(Member.class, MemberDTO.class);

        typeMap.addMappings(mapper -> {
            mapper.map(Member::getEmail, MemberDTO::setEmail); // email 매핑
            mapper.map(Member::getPw, MemberDTO::setPw); // pw 매핑
            mapper.map(Member::getName, MemberDTO::setName); // name 매핑
            mapper.map(Member::getAddress, MemberDTO::setAddress); // address 매핑
            mapper.map(src -> (
                            src.getMemberRoleList() == null
                                    ? new ArrayList<>()
                                    : src.getMemberRoleList().stream()
                                    .map(Enum::name)
                                    .collect(Collectors.toList())),
                    MemberDTO::setRoleNames); // roleNames 매핑
        });

        return modelMapper;
    }
}
