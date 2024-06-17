package com.group17.comic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ComicServiceTestsWithTruyenFull extends ComicServiceTests {
    private final UUID pluginId = UUID.fromString("123e4567-e89b-12d3-a456-426614173002");

    @Test
    void testValid_searchOnlyKeyword_ReturnList() {
        String keyword = "Tiên";
        var chapters = super.searchComic(pluginId, keyword, "", 1);
        assertThat(chapters).isNotNull();
        assertThat(chapters.getData()).isNotNull();
        assertThat(chapters.getPagination()).isNotNull();
        assertThat(chapters.getData().size()).isNotZero();
        assertThat(chapters.getMeta().size()).isNotZero();
    }

    @Test
    void testValid_searchOnlyByGenre_ReturnList() {
        String genre = "do-thi";
        var chapters = super.searchComic(pluginId, "", genre, 1);
        assertThat(chapters).isNotNull();
        assertThat(chapters.getData()).isNotNull();
        assertThat(chapters.getPagination()).isNotNull();
        assertThat(chapters.getData().size()).isNotZero();
    }

    @Test
    void testValid_searchByKeywordAndGenre_ReturnList() {
        String keyword = "Tiên Nghịch";
        String genre = "tien-hiep";
        var chapters = super.searchComic(pluginId, keyword, genre, 1);
        assertThat(chapters).isNotNull();
        assertThat(chapters.getData()).isNotNull();
        assertThat(chapters.getPagination()).isNotNull();
        assertThat(chapters.getData().size()).isNotZero();
    }

    @Test
    void testInvalid_searchComic_ReturnException() {
        String invalidKeyword = "invalid-keyword";
        String invalidGenre = "invalid-genre";
        try {
            var chapters = super.searchComic(pluginId, invalidKeyword, invalidGenre, 1);
        } catch (Exception ex) {
            assertThat(ex).isNotNull();
        }
    }
}
