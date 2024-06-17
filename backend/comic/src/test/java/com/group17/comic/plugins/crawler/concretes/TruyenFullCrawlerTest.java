package com.group17.comic.plugins.crawler.concretes;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.group17.comic.dtos.request.AlternatedChapterRequest;
import com.group17.comic.exceptions.BusinessException;
import com.group17.comic.models.*;

class TruyenFullCrawlerTest {
    private TruyenFullCrawler truyenFullCrawler;

    @BeforeEach
    void setUp() {
        truyenFullCrawler = new TruyenFullCrawler();
    }

    @Test
    void canGetLastedComics() {
        int currentPage = 1;
        PageableData<Integer, List<LatestComic>> crawledComic = truyenFullCrawler.getLastedComics(currentPage);
        Assertions.assertNotNull(crawledComic.getData());
        Assertions.assertEquals(1, crawledComic.getPagination().getCurrentPage());
    }

    @Test
    void canGetChapters() {
        String comicTagId = "9539";
        int currentPage = 1;
        PageableData<Integer, List<Chapter>> chapterDataModel = truyenFullCrawler.getChapters(comicTagId, 1);
        Assertions.assertNotNull(chapterDataModel);
        Assertions.assertEquals(1, chapterDataModel.getPagination().getCurrentPage());
        Assertions.assertNotNull(chapterDataModel.getData());
    }

    @Test
    void getComicInfoOnOtherServer() {
        String title = "Tiêu Tổng, Xin Tha Cho Tôi";
        String authorName = "Thục Kỷ";
        String comicTagId = "truyen-tieu-tong-xin-tha-cho-toi";
        int chapterNumber = 2;

        AlternatedChapterRequest alternatedChapterDTO =
                new AlternatedChapterRequest(title, authorName, comicTagId, chapterNumber);
        TruyenChuTHCrawler truyenChuTHCrawler = new TruyenChuTHCrawler();
        TangThuVienCrawler tangThuVienCrawler = new TangThuVienCrawler();
        Comic comicOnTruyenChu = truyenChuTHCrawler.getComicInfoOnOtherServer(alternatedChapterDTO);
        Comic comicOnTangThuVien = null;
        try {
            comicOnTangThuVien = tangThuVienCrawler.getComicInfoOnOtherServer(alternatedChapterDTO);
        } catch (BusinessException ex) {
            comicOnTangThuVien = null;
        }

        Assertions.assertNull(comicOnTangThuVien);
        Assertions.assertNotNull(comicOnTruyenChu);
        Assertions.assertEquals(comicOnTruyenChu.getTitle(), title);
        Assertions.assertEquals(authorName, comicOnTruyenChu.getAuthor().getName());
    }

    @Test
    void canGetComicChapterContent() {
        // given
        String tagId = "36595";
        String currentChapter = "4717189";

        PageableData<Integer, ComicChapterContent> contentDataModel =
                truyenFullCrawler.getComicChapterContent(tagId, currentChapter);
        ComicChapterContent chapterContent = contentDataModel.getData();
        Assertions.assertEquals(1, chapterContent.getChapterNumber());
        Assertions.assertEquals("Đối chọi", chapterContent.getChapterTitle());
        Assertions.assertEquals("Vũng Nước Đục", chapterContent.getTitle());
        Assertions.assertEquals("Lạc Hồi", chapterContent.getAuthor().getName());
    }
}
