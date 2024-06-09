package com.group17.comic.service;

import java.util.List;
import java.util.UUID;

import com.group17.comic.dtos.request.AlternatedChapterRequest;
import com.group17.comic.dtos.response.AuthorResponse;
import com.group17.comic.models.Chapter;
import com.group17.comic.models.Comic;
import com.group17.comic.models.ComicChapterContent;
import com.group17.comic.models.ComicModel;
import com.group17.comic.models.DataModel;
import com.group17.comic.models.DataSearchModel;
import com.group17.comic.models.Genre;

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
