package com.korea.shop.controller;

import com.korea.shop.dto.CustomPage;
import com.korea.shop.dto.ItemDTO;
import com.korea.shop.service.ItemService;
import com.korea.shop.util.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.query.Page;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Log4j2
public class ItemController {

    private final ItemService itemService;
    private final CustomFileUtil fileUtil;

//    @PreAuthorize("hasAnyRole('ROLE_USER')") // 예시
//    @GetMapping
//    public ResponseEntity<List<ItemDTO>> getAllItems(@PageableDefault) {
//        return ResponseEntity.ok(itemService.getAllItems());
//    }

    // 아이템 상세보기
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable Long id) {

        return ResponseEntity.ok(itemService.getItem(id));
    }

    // 페이징 처리 전체 아이템 보기
    @GetMapping("/list")
    public ResponseEntity<CustomPage<ItemDTO>>
    getAllItems(@PageableDefault(page = 0, size = 3) Pageable pageable){
        return ResponseEntity.ok(itemService.getAllItemsPaged(pageable));
    }

    // 아이템 생성
    @PostMapping
    public ResponseEntity<ItemDTO> createItem(ItemDTO itemDTO) {
        itemService.saveItem(itemDTO);
        return ResponseEntity.ok(itemDTO);
    }

    // 아이템 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    // 아이템 변경
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        itemService.updateItem(id, itemDTO);
        return ResponseEntity.ok(itemService.getItem(id));
    }

    // 이름으로 아이템 찾기
    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> getItemsByName(@RequestParam String name) {
        return ResponseEntity.ok(itemService.getItemsByName(name));
    }

    // 아이디로 아이템 찾기
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.existsById(id));
    }

    // 업로드 사진 보여주기
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable String fileName){
        return fileUtil.getFile(fileName);
    }
}
