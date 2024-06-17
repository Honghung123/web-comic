package com.group17.comic.service;

import java.util.List;
import java.util.UUID;

import com.group17.comic.dtos.request.AlternatedChapterRequest;
import com.group17.comic.dtos.response.AuthorResponse;
import com.group17.comic.models.Chapter;
import com.group17.comic.models.Comic;
import com.group17.comic.models.ComicChapterContent;
import com.group17.comic.models.Genre;
import com.group17.comic.models.LatestComic;
import com.group17.comic.models.PageableData;
import com.group17.comic.models.SearchingPageableData;

public interface IComicService {
    List<Genre> getAllGenres(UUID pluginId);

    PageableData<Integer, List<LatestComic>> getNewestCommic(UUID pluginId, int page);

    PageableData<Integer, List<LatestComic>> getComicsOfAnAuthor(
            UUID serverId, String authorId, String tagId, int page);

    SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> searchComic(
            UUID serverId, String keyword, String byGenres, int currentPage);

    Comic getComicInfo(UUID pluginId, String tagUrl);

    PageableData<Integer, List<Chapter>> getChapters(UUID serverId, String tagId, int currentPage);

    Comic getComicInfoOnOtherServer(UUID serverId, AlternatedChapterRequest altChapterDto);

    PageableData<?, ComicChapterContent> getComicChapterContent(UUID serverId, String tagId, String currentChapter);

    PageableData<?, ComicChapterContent> getComicChapterContentOnOtherServer(
            UUID serverId, AlternatedChapterRequest altChapterDto);
}
