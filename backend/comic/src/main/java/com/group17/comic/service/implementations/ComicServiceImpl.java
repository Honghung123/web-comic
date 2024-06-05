package com.group17.comic.service.implementations;

import com.group17.comic.enums.ExceptionType;
import com.group17.comic.enums.PluginServiceType;
import com.group17.comic.exception.BusinessException;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.service.IComicService;
import com.group17.comic.service.ICrawlerPluginService;
import com.group17.comic.service.IExporterPluginService;
import org.springframework.stereotype.Service;

import com.group17.comic.dto.request.AlternatedChapterRequest;
import com.group17.comic.dto.response.AuthorResponse;
import com.group17.comic.model.*;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import java.util.List;
import java.util.UUID;

@Service("comicServiceV1")
@RequiredArgsConstructor
public class ComicServiceImpl implements IComicService {
    private final PluginServiceProviderImpl supportComicService;

    private ICrawlerPluginService getCrawlerPluginService() {
        return (ICrawlerPluginService) supportComicService
                .getPluginServiceByType(PluginServiceType.CRAWLER_SERVICE);
    }

    private IExporterPluginService getExporterPluginService() {
        return (IExporterPluginService) supportComicService
                .getPluginServiceByType(PluginServiceType.EXPORTER_SERVICE);
    }

    private IDataCrawler getCrawlerPlugin(UUID pluginId){
        var crawlerService = this.getCrawlerPluginService();
        return (IDataCrawler) crawlerService.getPluginById(pluginId);
    }

    private UUID getDefaultPluginIdIfNull(UUID id, PluginServiceType pluginType) {
        if (id == null){
            switch (pluginType) {
                case CRAWLER_SERVICE ->
                        this.supportComicService.getDefaultPluginId(this.getCrawlerPluginService());
                case EXPORTER_SERVICE ->
                        this.supportComicService.getDefaultPluginId(this.getExporterPluginService());
                default -> throw new BusinessException(ExceptionType.INVALID_PLUGIN_SERVICE_TYPE);
            }
        }
        return id;
    }

    @SneakyThrows
    @Override
    public List<Genre> getAllGenres(UUID pluginId) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId, PluginServiceType.CRAWLER_SERVICE);
        return this.getCrawlerPlugin(pluginId).getGenres();
    }

    @SneakyThrows
    @Override
    public DataModel<Integer, List<ComicModel>> getNewestCommic(UUID pluginId, int page) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId, PluginServiceType.CRAWLER_SERVICE);
        return this.getCrawlerPlugin(pluginId).getLastedComics(page);
    }

    @SneakyThrows
    @Override
    public Comic getComicInfo(UUID pluginId, String tagUrl) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId, PluginServiceType.CRAWLER_SERVICE);
        return this.getCrawlerPlugin(pluginId).getComicInfo(tagUrl);
    }


    @SneakyThrows
    @Override
    public DataSearchModel<Integer, List<ComicModel>, List<AuthorResponse>> searchComic(UUID pluginId,
                                                                                        String keyword, String byGenres, int currentPage) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId, PluginServiceType.CRAWLER_SERVICE);
        return this.getCrawlerPlugin(pluginId).search(keyword, byGenres, currentPage);
    }

    @SneakyThrows
    @Override
    public DataModel<Integer, List<Chapter>> getChapters(UUID pluginId, String tagId, int currentPage) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId, PluginServiceType.CRAWLER_SERVICE);
        return this.getCrawlerPlugin(pluginId).getChapters(tagId, currentPage);
    }

    @SneakyThrows
    @Override
    public Comic getComicInfoOnOtherServer(UUID pluginId, AlternatedChapterRequest altChapterDto) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId, PluginServiceType.CRAWLER_SERVICE);
        return this.getCrawlerPlugin(pluginId).getComicInfoOnOtherServer(altChapterDto);
    }

    @SneakyThrows
    @Override
    public DataModel<?, ComicChapterContent> getComicChapterContent(UUID pluginId, String tagId, String currentChapter) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId, PluginServiceType.CRAWLER_SERVICE);
        return this.getCrawlerPlugin(pluginId).getComicChapterContent(tagId, currentChapter);
    }

    @Override
    public DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(UUID pluginId,
            AlternatedChapterRequest altChapterDto) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId, PluginServiceType.CRAWLER_SERVICE);
        return this.getCrawlerPlugin(pluginId).getComicChapterContentOnOtherServer(altChapterDto);
    }

    @Override
    public DataModel<Integer, List<ComicModel>> getComicsOfAnAuthor(UUID pluginId, String authorId, String tagId, int page) {
        pluginId = this.getDefaultPluginIdIfNull(pluginId, PluginServiceType.CRAWLER_SERVICE);
        return this.getCrawlerPlugin(pluginId).getComicsByAuthor(authorId, tagId, page);
    }
}
