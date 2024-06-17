package com.group17.comic.service.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.group17.comic.dtos.request.AlternatedChapterRequest;
import com.group17.comic.dtos.response.AuthorResponse;
import com.group17.comic.models.*;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.service.*;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service("comicServiceV1")
@RequiredArgsConstructor
public class ComicServiceImpl implements IComicService {
    private final ICrawlerPluginService crawlerPluginService;

    private IDataCrawler getConcretePlugin(UUID pluginId) {
        var res = crawlerPluginService.getPluginById(pluginId);
        return (IDataCrawler) res;
    }

    private UUID getDefaultPluginIdIfNull(UUID id) {
        if (id == null) {
            return crawlerPluginService.getDefaultPluginId();
        }
        return id;
    }

    @SneakyThrows
    @Override
    public List<Genre> getAllGenres(UUID pluginId) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId);
        return this.getConcretePlugin(pluginId).getGenres();
    }

    @SneakyThrows
    @Override
    public PageableData<Integer, List<LatestComic>> getNewestCommic(UUID pluginId, int page) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId);
        return this.getConcretePlugin(pluginId).getLastedComics(page);
    }

    @SneakyThrows
    @Override
    public Comic getComicInfo(UUID pluginId, String tagUrl) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId);
        return this.getConcretePlugin(pluginId).getComicInfo(tagUrl);
    }

    @SneakyThrows
    @Override
    public SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> searchComic(
            UUID pluginId, String keyword, String byGenres, int currentPage) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId);
        return this.getConcretePlugin(pluginId).search(keyword, byGenres, currentPage);
    }

    @SneakyThrows
    @Override
    public PageableData<Integer, List<Chapter>> getChapters(UUID pluginId, String tagId, int currentPage) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId);
        return this.getConcretePlugin(pluginId).getChapters(tagId, currentPage);
    }

    @SneakyThrows
    @Override
    public Comic getComicInfoOnOtherServer(UUID pluginId, AlternatedChapterRequest altChapterDto) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId);
        return this.getConcretePlugin(pluginId).getComicInfoOnOtherServer(altChapterDto);
    }

    @SneakyThrows
    @Override
    public PageableData<?, ComicChapterContent> getComicChapterContent(
            UUID pluginId, String tagId, String currentChapter) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId);
        return this.getConcretePlugin(pluginId).getComicChapterContent(tagId, currentChapter);
    }

    @Override
    public PageableData<?, ComicChapterContent> getComicChapterContentOnOtherServer(
            UUID pluginId, AlternatedChapterRequest altChapterDto) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId);
        return this.getConcretePlugin(pluginId).getComicChapterContentOnOtherServer(altChapterDto);
    }

    @Override
    public PageableData<Integer, List<LatestComic>> getComicsOfAnAuthor(
            UUID pluginId, String authorId, String tagId, int page) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId);
        return this.getConcretePlugin(pluginId).getComicsByAuthor(authorId, tagId, page);
    }
}
