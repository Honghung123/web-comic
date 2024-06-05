package com.group17.comic.service;

import java.util.List;
import java.util.UUID;

import com.group17.comic.dto.request.AlternatedChapterRequest;
import com.group17.comic.dto.response.AuthorResponse;
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
       DataSearchModel<Integer, List<ComicModel>, List<AuthorResponse>> searchComic(UUID serverId,
                                                                                    String keyword, String byGenres, int currentPage);
       Comic getComicInfo(UUID pluginId, String tagUrl);
       DataModel<Integer, List<Chapter>> getChapters(UUID serverId, String tagId, int currentPage);
       Comic getComicInfoOnOtherServer(UUID serverId, AlternatedChapterRequest altChapterDto);
       DataModel<?, ComicChapterContent> getComicChapterContent(UUID serverId,
                                                      String tagId, String currentChapter);
       DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(UUID serverId,
                                                      AlternatedChapterRequest altChapterDto);
}
