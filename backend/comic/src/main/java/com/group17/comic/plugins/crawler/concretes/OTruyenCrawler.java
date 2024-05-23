package com.group17.comic.plugins.crawler.concretes;

import com.group17.comic.dto.request.AlternatedChapterDTO;
import com.group17.comic.model.*;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.crawler.WebCrawler;
import com.group17.comic.utils.StringUtility;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OTruyenCrawler extends WebCrawler implements IDataCrawler {
    private final String BASE_API_URL = "https://otruyenapi.com/v1/api";
    private final UUID id = UUID.randomUUID();
    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public Integer getServerID() {
        return 3;
    }

    @Override
    public String getPluginName() {
        return "O Truyen";
    }

    @Override
    public List<Genre> getGenres() {
        return List.of();
    }

    @Override
    public DataModel<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage) {
        return null;
    }

    @Override
    public DataSearchModel<Integer, List<ComicModel>, List<Author>> search(String keyword, String byGenres, int currentPage) {
        return null;
    }

    //    @Override
//    public DataSearchModel<Integer, List<ComicModel>, List<Author>> search(String keyword, String byGenres, int currentPage) {
//        keyword = StringUtility.removeDiacriticalMarks(keyword);
//        if(StringUtils.hasLength(keyword) && StringUtils.hasLength(byGenre)) {
//            return searchByKeywordAndGenre(keyword, byGenre, currentPage);
//        }else if(keyword.isEmpty() && StringUtils.hasLength(byGenre)) {
//            return searchOnlyByGenre(byGenre, currentPage);
//        }else if(StringUtils.hasLength(keyword) && byGenre.isEmpty()) {
//            return searchOnlyByKeyword(keyword, currentPage);
//        }else {
//            return this.getHotOrPromoteComics(currentPage);
//        }
//    }
    @SneakyThrows
    public DataSearchModel<Integer, List<ComicModel>, List<Author>> searchByKeywordAndGenre(String keyword, String byGenres, int currentPage) {
        return new DataSearchModel<>(null, null, null);
    }
    @SneakyThrows
    public DataSearchModel<Integer, List<ComicModel>, List<Author>> searchOnlyByGenre(String byGenres, int currentPage) {
//        String apiUrl = BASE_API_URL + "/the-loai" + "/" + byGenres + "?page=" + currentPage;
//        List<ComicModel> matchedComics = new ArrayList<>();
//        RestTemplate request = new RestTemplate();
//        ResponseEntity<?> response = request.getForEntity(apiUrl, String.class);
        return new DataSearchModel<>(null, null, null);

    }

    @Override
    public DataModel<Integer, List<ComicModel>> getLastedComics(int currentPage) {
        return null;
    }

    @Override
    public Comic getComicInfo(String comicTagId) {
        return null;
    }

    @Override
    public Comic getComicInfoOnOtherServer(AlternatedChapterDTO altChapterDto) {
        return null;
    }

    @Override
    public DataModel<?, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter) {
        return null;
    }

    @Override
    public DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(AlternatedChapterDTO altChapterDto) {
        return null;
    }

    @Override
    public DataModel<Integer, List<ComicModel>> getComicsByAuthor(String authorId, int currentPage) {
        return null;
    }
}
