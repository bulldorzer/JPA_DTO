package com.korea.shop.service;

import com.korea.shop.domain.item.Album;
import com.korea.shop.domain.item.Book;
import com.korea.shop.domain.item.Item;
import com.korea.shop.domain.item.Movie;
import com.korea.shop.dto.ItemDTO;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@Transactional
class ItemServiceTest {

    @Autowired
    private ItemService itemService;
    @Autowired private ModelMapper modelMapper;

    private static final String[] BOOK_TITLES = { "자바의 정석", "스프링 부트 개론", "Effective Java", "Clean Code", "데이터베이스 설계" };
    private static final String[] MOVIE_TITLES = { "인셉션", "어벤져스", "인터스텔라", "기생충", "범죄도시" };
    private static final String[] ALBUM_TITLES = { "Love Yourself", "Map of the Soul", "Dynamite", "Proof", "Butter" };

    @Test
    void 아이템_더미데이터_생성() {
        Random random = new Random();

        IntStream.range(0, 20).forEach(i -> {
            int price = 10000 + random.nextInt(90000);
            int stockQuantity = 1 + random.nextInt(50);
            int itemType = random.nextInt(3); // 0: Book, 1: Movie, 2: Album

            Item item;
            if (itemType == 0) {
                String title = BOOK_TITLES[random.nextInt(BOOK_TITLES.length)];
                Book book = new Book();  // 기본 생성자 호출
                book.setName(title);
                book.setPrice(price);
                book.setStockQuantity(stockQuantity);
                book.setAuthor("저자" + i);
                book.setIsbn("ISBN-" + i);
                item = book;
            } else if (itemType == 1) {
                String title = MOVIE_TITLES[random.nextInt(MOVIE_TITLES.length)];
                Movie movie = new Movie();  // 기본 생성자 호출
                movie.setName(title);
                movie.setPrice(price);
                movie.setStockQuantity(stockQuantity);
                movie.setDirector("감독" + i);
                movie.setActor("배우" + i);
                item = movie;
            } else {
                String title = ALBUM_TITLES[random.nextInt(ALBUM_TITLES.length)];
                Album album = new Album();  // 기본 생성자 호출
                album.setName(title);
                album.setPrice(price);
                album.setStockQuantity(stockQuantity);
                album.setArtist("가수" + i);
                album.setEtc("기타정보" + i);
                item = album;
            }

            ItemDTO itemDTO = modelMapper.map(item, ItemDTO.class);
            itemService.saveItem(itemDTO); // ✅ repository 대신 service 사용
        });

        long count = itemService.getAllItems().size();
        assertThat(count).isEqualTo(20);
    }
}
