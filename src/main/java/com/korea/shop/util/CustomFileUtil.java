package com.korea.shop.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

    @Value("${com.korea.shop.upload.path}")
    private String uploadPath;

    // 객체 생성되면서 호출될 메서드 => 시작되면서 수행될일
    @PostConstruct
    public void init(){
        File temFolder = new File(uploadPath);
        
        if (temFolder.exists() == false){
            temFolder.mkdir(); // 디렉토리 생성
        }

        uploadPath = temFolder.getAbsolutePath();

        log.info("===============<CustomFileUtil>===============");
        log.info(uploadPath);
    }


    // 파일저장
    public List<String> saveFiles(List<MultipartFile> files)throws RuntimeException{

        if (files == null || files.size() == 0){
            return List.of();
        }

        List<String> uploadNames = new ArrayList<>();

        for (MultipartFile multipartFile : files){
            String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();

            Path savePath = Paths.get(uploadPath, savedName);

            try {
                Files.copy(multipartFile.getInputStream(), savePath);
                uploadNames.add(savedName);

            }catch (IOException e){
                throw new RuntimeException(e.getMessage());
            }
        } // for end

        return uploadNames;

    }

}
