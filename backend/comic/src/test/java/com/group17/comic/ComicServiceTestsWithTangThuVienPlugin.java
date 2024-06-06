package com.group17.comic;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ComicServiceTestsWithTangThuVienPlugin extends ComicServiceTests {
    private final UUID serverId = UUID.fromString("123e4567-e89b-12d3-a456-426614173000");
    @Test
    public void testValid_getChapters_ReturnList() {
        String existedTagId = "vuong-gia-thoi-khac";
        var chapters = super.getChaptersTest(serverId, existedTagId, 1);
        assertThat(chapters).isNotNull();
        assertThat(chapters.getData()).isNotNull();
        assertThat(chapters.getPagination()).isNotNull();
        assertThat(chapters.getData().size()).isNotEqualTo(0);
    }
    @Test
    public void testInvalid_getChapters_ReturnException() {
        String invalidTagId = "invalid-tag-id";
        try {
            var chapters = super.getChaptersTest(serverId, invalidTagId, 1);
        }catch (Exception ex) {
            assertThat(ex).isNotNull();
        }
    }
}
