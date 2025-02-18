package com.korea.shop.service;

import com.korea.shop.domain.item.Item;
import com.korea.shop.dto.ItemDTO;

import java.util.List;

public interface ItemService {
    void saveItem(ItemDTO itemDTO) ;
    List<ItemDTO> getAllItems();
    ItemDTO getItem(Long id);
    void deleteItem(Long id);
    void updateItem(Long id, ItemDTO itemDTO);
    List<ItemDTO> getItemsByName(String name);
    boolean existsById(Long id);
}
