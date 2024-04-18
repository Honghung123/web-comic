package com.group17.comic.plugins;
 
import java.util.List;

import com.group17.comic.model.*; 

public interface IDataCrawler {     
    List<Genre> getGenres();
    DataModel<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage);
    DataSearchModel<Integer, List<ComicModel>, List<Author>> search(String keyword, int currentPage);
    DataModel<Integer, List<ComicModel>> getLastedComics(int currentPage);
    Comic getComicInfo(String comicTagId);
    DataModel<?, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter);
}
