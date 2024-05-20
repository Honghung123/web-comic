package com.group17.comic;

import com.group17.comic.model.Chapter;
import com.group17.comic.model.DataModel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ComicServiceTestsWithTangThuVienPlugin extends ComicServiceTests {
    private final int pluginId = 0;
    @Test
    public void getChaptersTest() {
        String existedTagId = "vuong-gia-thoi-khac";
        var chapters = super.getChaptersTest(pluginId, existedTagId, 1);
        assertThat(chapters).isNotNull();
        assertThat(chapters.getData()).isNotNull();
        assertThat(chapters.getPagination()).isNotNull();
        assertThat(chapters.getData().size()).isNotEqualTo(0);
        try{
            String notExistedTagId = "not-existed-tag-id";
            chapters = super.getChaptersTest(pluginId, notExistedTagId, 1);
        }
        catch (Exception e){
            assertThat(e).isNotNull();
        }
    }
}
