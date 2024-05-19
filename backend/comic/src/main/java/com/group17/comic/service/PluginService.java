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
public class PluginService implements IPluginService {
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
        String crawlerRelativePath = projectDirectory + crawlerDirectory;
        var crawlerClasses = PluginUtility.getAllPluginsFromFolderWithoutInstantiation(
                                crawlerRelativePath, crawlerPackageName, IDataCrawler.class);
        if (crawlers.size() == 0 || crawlerClasses.size() != crawlers.size()) {
            crawlers = PluginUtility.getAllPluginsFromFolder(crawlerRelativePath, crawlerPackageName,
                    IDataCrawler.class);
        }
    }

    @SneakyThrows
    @Override
    public List<CrawlerPlugin> getAllCrawlerPlugins() {
        this.checkCrawlerPlugins();
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
        var exporterClasses = PluginUtility.getAllPluginsFromFolderWithoutInstantiation(
                converterRelativePath, converterPackageName, IFileConverter.class);
        if (exporters.size() == 0 || exporterClasses.size() != exporters.size()) {
            exporters = PluginUtility.getAllPluginsFromFolder(converterRelativePath, 
                        converterPackageName, IFileConverter.class);
        }
    }

    @SneakyThrows
    @Override
    public List<ConverterPlugin> getAllConverterPlugins() {
        this.checkConverterPlugins();
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
        return exporters.get(converterId).getConvertedFile(chapterDto);
    }

    @SneakyThrows
    public void checkCrawlerServerSize(int crawlersSize) {
        this.checkCrawlerPlugins();
        if (crawlersSize != crawlers.size()) {
            throw new InvalidPluginListException("Server has changed. Please refresh your page");
        }
    }

    @SneakyThrows
    @Override
    public void checkConverterPluginSize(int convertersSize) {
        this.checkConverterPlugins();
        if (convertersSize != exporters.size()) {
            throw new InvalidPluginListException("Converter has changed. Please refresh your page");
        }
    }
}
