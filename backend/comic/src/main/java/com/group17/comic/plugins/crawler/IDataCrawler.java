package com.group17.comic.plugins.crawler;

import java.util.List;
import java.util.UUID;

import com.group17.comic.dtos.request.AlternatedChapterRequest;
import com.group17.comic.dtos.response.AuthorResponse;
import com.group17.comic.models.*;
import com.group17.comic.tagging_interfaces.IPluginType;

public interface IDataCrawler extends IPluginType {
    UUID getID();

    String getPluginName();

    List<Genre> getGenres();

    PageableData<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage);

    SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> search(
            String keyword, String byGenres, int currentPage);

    PageableData<Integer, List<LatestComic>> getLastedComics(int currentPage);

    Comic getComicInfo(String comicTagId);

    Comic getComicInfoOnOtherServer(AlternatedChapterRequest altChapterDto);

    PageableData<?, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter);

    PageableData<?, ComicChapterContent> getComicChapterContentOnOtherServer(AlternatedChapterRequest altChapterDto);

    PageableData<Integer, List<LatestComic>> getComicsByAuthor(String authorId, String tagId, int currentPage);
}
