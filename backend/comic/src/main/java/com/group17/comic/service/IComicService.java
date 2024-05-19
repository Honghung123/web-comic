package com.group17.comic.service;

import java.util.List;

import com.group17.comic.dto.request.AlternatedChapterDTO;
import com.group17.comic.model.Author;
import com.group17.comic.model.Chapter;
import com.group17.comic.model.Comic;
import com.group17.comic.model.ComicChapterContent;
import com.group17.comic.model.ComicModel;
import com.group17.comic.model.DataModel;
import com.group17.comic.model.DataSearchModel;
import com.group17.comic.model.Genre;

public interface IComicService { 
      public List<Genre> getAllGenres(int pluginId);
      public DataModel<Integer, List<ComicModel>> getNewestCommic(int pluginId, int page);
      public DataModel<Integer, List<ComicModel>> getComicsOfAnAuthor(int serverId, String authorId, int page);
      public DataSearchModel<Integer, List<ComicModel>, List<Author>> searchComic(int serverId,
                                                      String keyword, String byGenres, int currentPage);
      public Comic getComicInfo(int pluginId, String tagUrl);
      public DataModel<Integer, List<Chapter>> getChapters(int serverId, String tagId, int currentPage);
      public Comic getComicInfoOnOtherServer(int serverId, AlternatedChapterDTO altChapterDto);
      public DataModel<?, ComicChapterContent> getComicChapterContent(int serverId,
                                                      String tagId, String currentChapter);
      public DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(int serverId,
                                                      AlternatedChapterDTO altChapterDto);
}
