package com.korea.shop.service;

import com.korea.shop.domain.item.Album;
import com.korea.shop.domain.item.Book;
import com.korea.shop.domain.item.Item;
import com.korea.shop.domain.item.Movie;
import com.korea.shop.dto.CustomPage;
import com.korea.shop.dto.ItemDTO;
import com.korea.shop.repository.ItemRepository;
import com.korea.shop.util.NoDataFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public void saveItem(ItemDTO itemDTO) {
        Item item = transferItem(itemDTO);
        itemRepository.save(item);
    }

    public Item transferItem(ItemDTO itemDTO){
        Item item = null;
        switch (itemDTO.getDtype()) {
            case "B":
                item = Book.builder()
                        .name(itemDTO.getName())
                        .price(itemDTO.getPrice())
                        .stockQuantity(itemDTO.getStockQuantity())
                        .author(itemDTO.getAuthor())
                        .isbn(itemDTO.getIsbn())
                        .build();
                break;
            case "M":
                item = Movie.builder()
                        .name(itemDTO.getName())
                        .price(itemDTO.getPrice())
                        .stockQuantity(itemDTO.getStockQuantity())
                        .director(itemDTO.getDirector())
                        .actor(itemDTO.getActor())
                        .build();
                break;
            case "A":
                item = Album.builder()
                        .name(itemDTO.getName())
                        .price(itemDTO.getPrice())
                        .stockQuantity(itemDTO.getStockQuantity())
                        .artist(itemDTO.getArtist())
                        .etc(itemDTO.getEtc())
                        .build();
                break;
            default:
                throw new IllegalArgumentException("Invalid dtype: " + itemDTO.getDtype());
        }
        return item;
    }

    @Override
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map( item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList());
    }

    // 페이징 처리 아직 작업 안끝남
    @Override
    public CustomPage<ItemDTO> getAllItemsPaged(Pageable pageable) {

        Page<Item> itemPage = itemRepository.findAll(pageable); // Optional 자료형

        // 예외처리 구문
        if (itemPage.isEmpty()){
            throw new NoDataFoundException("조회된 데이터가 없습니다.");
        }
        Page<ItemDTO> dtoPage= itemPage.map(item -> modelMapper.map(item, ItemDTO.class));

        // DTO에 페이지네이션 정보 추가 ( 별도의 DTO 만들기)
        int groupSize = 10; // 한그룹의 표시할 페이지 개수 10개
        return CustomPage.of(dtoPage, groupSize);
    }

    private List<Integer> getPaginationRange(int currentPage, int totalPages, int groupSize){
        int currentGroup = (currentPage/groupSize) * groupSize;
        int startPage = currentPage-1;
        int endPage = Math.min(startPage + groupSize -1, totalPages);

        return IntStream.rangeClosed(startPage, endPage)
                .boxed()
                .collect(Collectors.toList());


    }

    @Override
    public ItemDTO getItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return modelMapper.map(item, ItemDTO.class);
    }

    @Transactional
    @Override
    public void deleteItem(Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
        } else {
            throw new RuntimeException("Item not found");
        }
    }

    @Transactional
    @Override
    public void updateItem(Long id, ItemDTO itemDTO) {
        // 기존데이터를 불러와서 데이터를 수정하면 - 감지
        Item searchItem = itemRepository.findById(id).orElseThrow();

        searchItem.setName(itemDTO.getName());
        searchItem.setPrice(itemDTO.getPrice());
        searchItem.setStockQuantity(itemDTO.getStockQuantity());

        switch (itemDTO.getDtype()){
            case "A" :
                Album album = (Album)searchItem;
                album.setArtist(itemDTO.getArtist());
                album.setArtist(itemDTO.getArtist());
                break;
            case "B" :
                Book book = (Book)searchItem;
                book.setIsbn(itemDTO.getIsbn());
                book.setAuthor(itemDTO.getAuthor());
                break;
            case "M" :
                Movie movie = (Movie) searchItem;
                movie.setActor(itemDTO.getActor());
                movie.setDirector(itemDTO.getDirector());
                break;
            default:
                throw new IllegalArgumentException("Invalid dtype: " + itemDTO.getDtype());
        }
        // jpa가 변경 감지를 사용해서 save할 필요가 없음!

    }

    @Override
    public List<ItemDTO> getItemsByName(String name) {
        return itemRepository.findByName(name).stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return itemRepository.existsById(id);
    }
}
