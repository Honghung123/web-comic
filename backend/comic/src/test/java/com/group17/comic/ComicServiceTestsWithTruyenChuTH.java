package com.group17.comic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ComicServiceTestsWithTruyenChuTH extends ComicServiceTests {
    private final UUID pluginId = UUID.fromString("123e4567-e89b-12d3-a456-426614173001");

    @Test
    void test_getLastestComic_ReturnList() {
        var chapters = super.getNewestComic(pluginId, 1);
        assertThat(chapters).isNotNull();
        assertThat(chapters.getData()).isNotNull();
        assertThat(chapters.getPagination()).isNotNull();
        assertThat(chapters.getData().size()).isNotZero();
    }
}
