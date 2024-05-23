package com.group17.comic.plugins.crawler;
 
import java.util.List;
import java.util.UUID;

import com.group17.comic.dto.request.AlternatedChapterDTO;
import com.group17.comic.model.*; 

public interface IDataCrawler {
    UUID getUUID();
    String getPluginName();
    List<Genre> getGenres();
    DataModel<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage);
    DataSearchModel<Integer, List<ComicModel>, List<Author>> search(String keyword, String byGenres, int currentPage);
    DataModel<Integer, List<ComicModel>> getLastedComics(int currentPage);
    Comic getComicInfo(String comicTagId);
    Comic getComicInfoOnOtherServer(AlternatedChapterDTO altChapterDto);
    DataModel<?, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter);
    DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(AlternatedChapterDTO altChapterDto);
    DataModel<Integer, List<ComicModel>> getComicsByAuthor(String authorId, int currentPage);
}
