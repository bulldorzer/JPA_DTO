package com.korea.shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

    private String name;
    private int price;

    @JsonProperty("stockQty")
    private int stockQuantity;

    private String dtype; // 구분 문자

    // 책
    private String author;
    private String isbn;

    // 앨범
    private String artist;
    private String etc;

    // 무비
    private String director;
    private String actor;
    
    // 글 삭제 여부
    private boolean delFlag;
    
    // 업로드 파일들
    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();

    // 업로드 파일 이름들
    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();



}
