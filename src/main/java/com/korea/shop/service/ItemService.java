package com.korea.shop.service;

import com.korea.shop.domain.item.Item;
import com.korea.shop.dto.CustomPage;
import com.korea.shop.dto.ItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    void saveItem(ItemDTO itemDTO) ;
    public List<ItemDTO> getAllItems();
    public CustomPage<ItemDTO> getAllItemsPaged(Pageable pageable);
    ItemDTO getItem(Long id);
    void deleteItem(Long id);
    void updateItem(Long id, ItemDTO itemDTO);
    List<ItemDTO> getItemsByName(String name);
    boolean existsById(Long id);
}
