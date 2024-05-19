package com.group17.comic.service; 

import org.springframework.stereotype.Service;

import com.group17.comic.dto.request.AlternatedChapterDTO;
import com.group17.comic.enums.Plugin; 
import com.group17.comic.model.*;
import com.group17.comic.plugins.crawler.IDataCrawler; 

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import java.util.List;

@Service("comicServiceV1")
@RequiredArgsConstructor
public class ComicService implements IComicService{
    private final IPluginService pluginService; 

    private IDataCrawler getCrawlerPlugin(int pluginId) {
          var plugin = pluginService.getPlugin(Plugin.CRAWLER, pluginId);
          return (IDataCrawler) plugin;
    }

    @SneakyThrows
    @Override
    public List<Genre> getAllGenres(int pluginId) { 
        var result = this.getCrawlerPlugin(pluginId).getGenres();
        return result;
    }

    @SneakyThrows
    @Override
    public DataModel<Integer, List<ComicModel>> getNewestCommic(int pluginId, int page) { 
        var result = this.getCrawlerPlugin(pluginId).getLastedComics(page);
        return result;
    }

    @SneakyThrows
    @Override
    public Comic getComicInfo(int pluginId, String tagUrl) { 
        var comic = this.getCrawlerPlugin(pluginId).getComicInfo(tagUrl);
        return comic;
    }

    @SneakyThrows
    @Override
    public DataSearchModel<Integer, List<ComicModel>, List<Author>> searchComic(int pluginId,
            String keyword, String byGenres, int currentPage) { 
        var result = this.getCrawlerPlugin(pluginId).search(keyword, byGenres, currentPage);
        return result;
    }

    @SneakyThrows
    @Override
    public DataModel<Integer, List<Chapter>> getChapters(int pluginId, String tagId, int currentPage) {
        var result = this.getCrawlerPlugin(pluginId).getChapters(tagId, currentPage);
        return result;
    }

    @SneakyThrows
    @Override
    public Comic getComicInfoOnOtherServer(int pluginId, AlternatedChapterDTO altChapterDto) {
        var result = this.getCrawlerPlugin(pluginId).getComicInfoOnOtherServer(altChapterDto);
        return result;
    }

    @SneakyThrows
    @Override
    public DataModel<?, ComicChapterContent> getComicChapterContent(int pluginId, String tagId, String currentChapter) {
        var result = this.getCrawlerPlugin(pluginId).getComicChapterContent(tagId, currentChapter);
        return result;
    }

    @Override
    public DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(int pluginId,
            AlternatedChapterDTO altChapterDto) {
        var result = this.getCrawlerPlugin(pluginId).getComicChapterContentOnOtherServer(altChapterDto);
        return result;
    }

    @Override
    public DataModel<Integer, List<ComicModel>> getComicsOfAnAuthor(int pluginId, String authorId, int page) {
        var result = this.getCrawlerPlugin(pluginId).getComicsByAuthor(authorId, page);
        return result;
    }

}
