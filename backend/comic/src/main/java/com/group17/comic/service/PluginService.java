package com.group17.comic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.enums.Plugin;
import com.group17.comic.exception.customs.InvalidPluginListException;
import com.group17.comic.model.*;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.exporter.IFileConverter;
import com.group17.comic.utils.PluginUtility;
  
import lombok.SneakyThrows;
import java.util.ArrayList;
import java.util.List;

@Service("pluginServiceV1")
public class PluginService implements IPluginService{
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
    private List<IFileConverter> exporters = new ArrayList<>();

    @Override
    public Object getPlugin(Plugin plugin, int pluginId) {
        switch (plugin) {
            case CRAWLER -> {
                return crawlers.get(pluginId);
            }
            case EXPORTER -> {
                return exporters.get(pluginId);
            }
            default ->
                throw new InvalidPluginListException("Plugin not found");
        } 
    }

    @SneakyThrows
    @Override
    public void checkCrawlerPlugins() {
        String pluginRelativePath = projectDirectory + crawlerDirectory;
        crawlers = PluginUtility.getAllPluginsFromFolder(pluginRelativePath, crawlerPackageName, IDataCrawler.class);
    }

    @SneakyThrows
    @Override
    public List<CrawlerPlugin> getAllCrawlerPlugins() {
        checkCrawlerPlugins();
        List<CrawlerPlugin> pluginList = new ArrayList<>();
        int index = 0;
        for (var crawler : crawlers) {
            String pluginName = crawler.getPluginName();
            pluginList.add(new CrawlerPlugin(index, pluginName));
            index++;
        }
        return pluginList;
    }

    @SneakyThrows
    private void checkConverterPlugins() {
        String converterRelativePath = projectDirectory + converterDirectory;
        exporters = PluginUtility.getAllPluginsFromFolder(converterRelativePath, converterPackageName,
                IFileConverter.class);
    }

    @SneakyThrows
    @Override
    public List<ConverterPlugin> getAllConverterPlugins() {
        checkConverterPlugins();
        List<ConverterPlugin> pluginList = new ArrayList<>();
        int index = 0;
        for (var converter : exporters) {
            String pluginName = converter.getPluginName();
            String blobType = converter.getBlobType();
            pluginList.add(new ConverterPlugin(index, pluginName, blobType));
            index++;
        }
        return pluginList;
    }

    @SneakyThrows
    @Override
    public ChapterFile exportFile(ChapterDTO chapterDto, int converterId) {
        checkConverterPlugins();
        return exporters.get(converterId).getConvertedFile(chapterDto);
    }

    @SneakyThrows
    public void checkCrawlerServerSize(int crawlersSize) {
        checkCrawlerPlugins();
        if (crawlersSize != crawlers.size()) {
            throw new InvalidPluginListException("Server has changed. Please refresh your page");
        }
    }

    @SneakyThrows
    @Override
    public void checkConverterPluginSize(int convertersSize) {
        checkConverterPlugins();
        if (convertersSize != exporters.size()) {
            throw new InvalidPluginListException("Converter has changed. Please refresh your page");
        }
    } 
}
