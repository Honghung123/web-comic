package com.group17.comic.plugins.crawler.concretes;

import com.group17.comic.dto.request.AlternatedChapterRequest;
import com.group17.comic.exception.BusinessException;
import com.group17.comic.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class TruyenFullCrawlerTest {
    private TruyenFullCrawler truyenFullCrawler;
    @BeforeEach
    void setUp() {
        truyenFullCrawler = new TruyenFullCrawler();
    }



    @Test
    void getServerID() {

    }

    @Test
    void getPluginName() {
    }

    @Test
    void getGenres() {
    }

    @Test
    void canGetLastedComics() {
        //given
        int currentPage = 1;

        //when
        DataModel<Integer, List<ComicModel>> crawledComic =truyenFullCrawler.getLastedComics(currentPage);
        Assertions.assertNotNull(crawledComic.getData());
        Assertions.assertEquals(crawledComic.getPagination().getCurrentPage(), 1);
    }

    @Test
    void canGetChapters() {
        String comicTagId = "9539";
        int currentPage = 1;
        DataModel<Integer, List<Chapter>> chapterDataModel = truyenFullCrawler.getChapters(comicTagId, 1);
        Assertions.assertNotNull(chapterDataModel);
        Assertions.assertEquals(chapterDataModel.getPagination().getCurrentPage(), 1);
        Assertions.assertEquals(chapterDataModel.getPagination().getPerPage(), chapterDataModel.getData().size());
        Assertions.assertNotNull(chapterDataModel.getData());
    }

    @Test
    void getComicInfoOnOtherServer() {
        String title = "Tiêu Tổng, Xin Tha Cho Tôi";
        String authorName = "Thục Kỷ";
        String comicTagId = "truyen-tieu-tong-xin-tha-cho-toi";
        int chapterNumber = 2;

        AlternatedChapterRequest alternatedChapterDTO = new AlternatedChapterRequest(title, authorName, comicTagId, chapterNumber);
        TruyenChuTHCrawler truyenChuTHCrawler = new TruyenChuTHCrawler();
        TangThuVienCrawler tangThuVienCrawler = new TangThuVienCrawler();
        Comic comicOnTruyenChu = truyenChuTHCrawler.getComicInfoOnOtherServer(alternatedChapterDTO);
        Comic comicOnTangThuVien = null;
        try{
            comicOnTangThuVien = tangThuVienCrawler.getComicInfoOnOtherServer(alternatedChapterDTO);
        } catch(BusinessException ex){
            comicOnTangThuVien = null;
        }

        Assertions.assertNull(comicOnTangThuVien);
        Assertions.assertNotNull(comicOnTruyenChu);
        Assertions.assertEquals(comicOnTruyenChu.getTitle(), title);
        Assertions.assertEquals(authorName, comicOnTruyenChu.getAuthor().getName());

    }

    @Test
    void canGetComicChapterContent() {
        //given
        String tagId = "36595";
        String currentChapter = "4717189";

        DataModel<Integer, ComicChapterContent> contentDataModel = truyenFullCrawler.getComicChapterContent(tagId, currentChapter);
        ComicChapterContent chapterContent = contentDataModel.getData();
        Assertions.assertEquals(chapterContent.getChapterNumber(), 1);
        Assertions.assertEquals(chapterContent.getChapterTitle(), "Đối chọi");
        Assertions.assertEquals(chapterContent.getTitle(), "Vũng Nước Đục");
        Assertions.assertEquals(chapterContent.getAuthor().getName(), "Lạc Hồi");
    }

    @Test
    void getComicChapterContentOnOtherServer() {
        String title = "Tiêu Tổng, Xin Tha Cho Tôi";
        String authorName = "Thục Kỷ";
        String comicTagId = "";
        int chapterNumber = 2;
//        truyenFullCrawler.getComicChapterContentOnOtherServer();
    }

    @Test
    void canComicsByAuthor() {
        String authorName = "Thục Kỷ";
        DataModel<Integer, List<ComicModel>> comicsOfTheAuthorModel =  truyenFullCrawler.getComicsByAuthor(authorName, "", 1);

    }
}