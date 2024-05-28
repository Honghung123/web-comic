package com.group17.comic.service;

import java.util.List;
import java.util.UUID;

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
       List<Genre> getAllGenres(UUID pluginId);
       DataModel<Integer, List<ComicModel>> getNewestCommic(UUID pluginId, int page);
       DataModel<Integer, List<ComicModel>> getComicsOfAnAuthor(UUID serverId, String authorId, String tagId, int page);
       DataSearchModel<Integer, List<ComicModel>, List<Author>> searchComic(UUID serverId,
                                                      String keyword, String byGenres, int currentPage);
       Comic getComicInfo(UUID pluginId, String tagUrl);
       DataModel<Integer, List<Chapter>> getChapters(UUID serverId, String tagId, int currentPage);
       Comic getComicInfoOnOtherServer(UUID serverId, AlternatedChapterDTO altChapterDto);
       DataModel<?, ComicChapterContent> getComicChapterContent(UUID serverId,
                                                      String tagId, String currentChapter);
       DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(UUID serverId,
                                                      AlternatedChapterDTO altChapterDto);
}
