package com.group17.comic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ComicServiceTestsWithTruyenChuTH extends ComicServiceTests{ 
    private final UUID pluginId = UUID.fromString("123e4567-e89b-12d3-a456-426614173001");

    @Test
    public void test_getLastestComic_ReturnList() {
        var chapters = super.getNewestComic(pluginId, 1);
        assertThat(chapters).isNotNull();
        assertThat(chapters.getData()).isNotNull();
        assertThat(chapters.getPagination()).isNotNull();
        assertThat(chapters.getData().size()).isNotEqualTo(0);
    }

}
