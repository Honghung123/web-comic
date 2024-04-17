package com.group17.comic.plugins;

import java.io.IOException;
import java.util.List;

import com.group17.comic.model.Chapter;
import com.group17.comic.model.Comic;
import com.group17.comic.model.ComicChapterContent;
import com.group17.comic.model.DataModel;
import com.group17.comic.model.Genre;
import com.group17.comic.model.ComicModel; 

public interface WebCrawler { 
    String alternateImage = "https://truyen.tangthuvien.vn/images/default-book.png";
    DataModel<List<ComicModel>> search(String keyword, int currentPage);
    List<Genre> getGenres();
    DataModel<List<ComicModel>> getLastedComics(int currentPage) throws IOException;
    Comic getComicInfo(String comicTagId);
    DataModel<List<Chapter>> getChapters(String comicTagId, int currentPage);
    DataModel<ComicChapterContent> getComicChapterContent(String comicTagId, int currentChapter);
}
