package com.korea.shop.controller;

import com.korea.shop.dto.CustomPage;
import com.korea.shop.dto.ItemDTO;
import com.korea.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

//    @PreAuthorize("hasAnyRole('ROLE_USER')") // 예시
//    @GetMapping
//    public ResponseEntity<List<ItemDTO>> getAllItems(@PageableDefault) {
//        return ResponseEntity.ok(itemService.getAllItems());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItem(id));
    }

    // 페이징 처리
    @GetMapping("/list")
    public ResponseEntity<CustomPage<ItemDTO>> getAllItems(@PageableDefault(page = 0, size = 3) Pageable pageable){
        return ResponseEntity.ok(itemService.getAllItemsPaged(pageable));
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
        itemService.saveItem(itemDTO);
        return ResponseEntity.ok(itemDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        itemService.updateItem(id, itemDTO);
        return ResponseEntity.ok(itemService.getItem(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> getItemsByName(@RequestParam String name) {
        return ResponseEntity.ok(itemService.getItemsByName(name));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.existsById(id));
    }
}
