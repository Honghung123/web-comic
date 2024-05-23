package com.group17.comic;


import org.junit.jupiter.api.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ComicServiceTestsWithTangThuVienPlugin extends ComicServiceTests {
    private final int serverId = 0;

    @Test
    public void getChaptersTest() {
        String existedTagId = "vuong-gia-thoi-khac";
        var chapters = super.getChaptersTest(serverId, existedTagId, 1);
        assertThat(chapters).isNotNull();
        assertThat(chapters.getData()).isNotNull();
        assertThat(chapters.getPagination()).isNotNull();
        assertThat(chapters.getData().size()).isNotEqualTo(0);

        try{
            String notExistedTagId = "not-existed-tag-id";
            chapters = super.getChaptersTest(serverId, notExistedTagId, 1);
        }
        catch (Exception e){
            assertThat(e).isNotNull();
        }
    }
    @Test
    public void shouldFetchAllGenre(){

    }
}
