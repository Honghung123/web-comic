package com.group17.comic.service;

import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Service;

import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile; 
import com.group17.comic.model.*;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.exporter.IFileConverter;
import com.group17.comic.utils.PluginUtility;

import lombok.SneakyThrows; 
import java.util.ArrayList;
import java.util.List;

@Service
public class PluginService {
    @Value("${comic.base_dir}")
    String projectDirectory;
    @Value("${comic.plugin.crawler.crawler_package_name}")
    String crawlerPackageName;
    @Value("${comic.plugin.crawler.crawler_directory}")
    String crawlerDirectory;
    private List<IDataCrawler> crawlers = new ArrayList<>();
    @Value("${comic.plugin.converter.converter_package_name}")
    String converterPackageName;
    @Value("${comic.plugin.converter.converter_directory}")
    String converterDirectory;
    private List<IFileConverter> converters = new ArrayList<>();

    @SneakyThrows
    private void checkCrawlerPlugins(){
        String pluginRelativePath = projectDirectory + crawlerDirectory;
        crawlers = PluginUtility.getAllPluginsFromFolder(pluginRelativePath, crawlerPackageName, IDataCrawler.class);
    }

    @SneakyThrows
    public List<Plugin> getAllCrawlerPlugins(){
        checkCrawlerPlugins();
        List<Plugin> pluginList = new ArrayList<>();
        int index = 0;
        for (var crawler : crawlers) {
            String pluginName = crawler.getPluginName();
            pluginList.add(new Plugin(index, pluginName));
            index++;
        }
        return pluginList;
    }

    @SneakyThrows
    public List<Genre> getAllGenres(int pluginId){
        checkCrawlerPlugins();
        var result = crawlers.get(pluginId).getGenres();
        return result;
    }

    @SneakyThrows
    public DataModel<Integer, List<ComicModel>> getNewestCommic(int pluginId, int page){
        checkCrawlerPlugins();
        var result = crawlers.get(pluginId).getLastedComics(page);
        return result;
    }

    @SneakyThrows
    public Comic getComicInfo(int pluginId, String tagUrl){
        checkCrawlerPlugins();
        var comic = crawlers.get(pluginId).getComicInfo(tagUrl);
        return comic;
    }

    @SneakyThrows
    public DataSearchModel<Integer, List<ComicModel>, List<Author>> searchComic(int serverId, 
            String keyword, int currentPage){
        checkCrawlerPlugins();
        var result = crawlers.get(serverId).search(keyword, currentPage);
        return result;
    }

    @SneakyThrows
    public DataModel<Integer, List<Chapter>> getChapters(int serverId, String tagId, int currentPage){
        checkCrawlerPlugins();
        var result = crawlers.get(serverId).getChapters(tagId, currentPage);
        return result;
    }

    @SneakyThrows
    public DataModel<?, ComicChapterContent> getComicChapterContent(int serverId, String tagId, String currentChapter) {
        checkCrawlerPlugins();
        var result = crawlers.get(serverId).getComicChapterContent(tagId, currentChapter);
        return result;
    }


    @SneakyThrows
    private void checkConverterPlugins(){
        String converterRelativePath = projectDirectory + converterDirectory;
        converters = PluginUtility.getAllPluginsFromFolder(converterRelativePath, converterPackageName, IFileConverter.class);
    }

    @SneakyThrows
    public List<Plugin> getAllConverterPlugins(){
        checkConverterPlugins();
        List<Plugin> pluginList = new ArrayList<>();
        int index = 0;
        for (var converter : converters) { 
            String pluginName = converter.getPluginName();
            pluginList.add(new Plugin(index, pluginName));
            index++;
        }
        return pluginList;
    }

    @SneakyThrows
    public ChapterFile exportFile(ChapterDTO chapterDto, int converterId) {
        checkConverterPlugins();
        return converters.get(converterId).getConvertedFile(chapterDto);
    }
}
