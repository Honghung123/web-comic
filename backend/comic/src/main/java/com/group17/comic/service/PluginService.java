package com.group17.comic.service;

import lombok.extern.slf4j.Slf4j; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.enums.Plugin;
import com.group17.comic.exception.customs.InvalidPluginListException;
import com.group17.comic.model.*;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.exporter.IFileConverter;
import com.group17.comic.utils.ListUtility;
import com.group17.comic.utils.PluginUtility;

import lombok.SneakyThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("pluginServiceV1")
@Slf4j
public class PluginService implements IPluginService { 
    String baseDir = System.getProperty("user.dir");
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
    public Object getPlugin(Plugin plugin, UUID pluginId) {
        switch (plugin) {
            case CRAWLER -> {
                for (IDataCrawler crawler : crawlers) {
                    log.info(crawler.getID() + " |||| " + pluginId);
                }
                return crawlers.stream()
                        .filter(crawler -> crawler.getID().equals(pluginId))
                        .findFirst()
                        .orElseThrow(() -> new InvalidPluginListException("Plugin  found"));
            }
            case EXPORTER -> { 
                return exporters.stream()
                        .filter(exporter -> exporter.getId().equals(pluginId))
                        .findFirst()
                        .orElseThrow(() -> new InvalidPluginListException("Plugin not found"));
            }
            default ->
                throw new InvalidPluginListException("Plugin not found");
        }
    }

    @SneakyThrows
    @Override
    public void checkCrawlerPlugins() {
        baseDir = PluginUtility.resolveAbsolutePath(System.getProperty("user.dir"));
//        String crawlerAbsolutePath = baseDir + projectDirectory + crawlerDirectory;
        Path crawlerAbsolutePath = Paths.get(baseDir, projectDirectory, crawlerDirectory);
        var crawlerClasses = PluginUtility.getAllPluginsFromFolderWithoutInstantiation(
                                crawlerAbsolutePath.toString(), crawlerPackageName, IDataCrawler.class);
        if (crawlers.isEmpty() || crawlerClasses.size() != crawlers.size()) {
            crawlers = PluginUtility.getAllPluginsFromFolder(crawlerAbsolutePath.toString(), crawlerPackageName,
                                IDataCrawler.class);
        }
    }

    @SneakyThrows
    @Override
    public List<CrawlerPlugin> getAllCrawlerPlugins() {
        this.checkCrawlerPlugins();
        List<CrawlerPlugin> pluginList = new ArrayList<>();
        for (var crawler : crawlers) {
            String pluginName = crawler.getPluginName();
            UUID id = crawler.getID();
            pluginList.add(new CrawlerPlugin(id, pluginName));
        }
        return pluginList;
    }

    @SneakyThrows
    private void checkConverterPlugins() {
        baseDir = PluginUtility.resolveAbsolutePath(System.getProperty("user.dir"));
//        String converterAbsolutePath = baseDir + projectDirectory + converterDirectory;
        Path converterAbsolutePath = Paths.get(baseDir, projectDirectory, converterDirectory);
        var exporterClasses = PluginUtility.getAllPluginsFromFolderWithoutInstantiation(
                            converterAbsolutePath.toString(), converterPackageName, IFileConverter.class);
        if (exporters.isEmpty() || exporterClasses.size() != exporters.size()) {
            exporters = PluginUtility.getAllPluginsFromFolder(converterAbsolutePath.toString(),
                        converterPackageName, IFileConverter.class);
        }
    }

    @SneakyThrows
    @Override
    public List<ConverterPlugin> getAllConverterPlugins() {
        this.checkConverterPlugins();
        List<ConverterPlugin> pluginList = new ArrayList<>();
        for (var converter : exporters) {
            String pluginName = converter.getPluginName();
            String blobType = converter.getBlobType();
            UUID id = converter.getId();
            pluginList.add(new ConverterPlugin(id, pluginName, blobType));
        }
        return pluginList;
    }

    @SneakyThrows
    @Override
    public ChapterFile exportFile(ChapterDTO chapterDto, UUID converterId) {
        return exporters.stream()
                .filter(exporter -> exporter.getId().equals(converterId))
                .findFirst()
                .orElseThrow(() -> new InvalidPluginListException("Plugin not found "))
                .getConvertedFile(chapterDto);
    }

    @Override
    public UUID getPluginIdByName(String name) {
        Optional<IDataCrawler> matchedCrawler = crawlers.stream()
                .filter(crawler -> crawler.getPluginName().equals(name))
                .findFirst();
        if (matchedCrawler.isPresent()) {
            return matchedCrawler.get().getID();
        }
        Optional<IFileConverter> matchedConverter = exporters.stream()
                .filter(exporter -> exporter.getPluginName().equals(name))
                .findFirst();
        if (matchedConverter.isPresent()) {
            return matchedConverter.get().getId();
        }
        throw new InvalidPluginListException("Plugin not found");
    }

    @SneakyThrows
    public void checkCrawlerList(List<String> crawlersList) {
        this.checkCrawlerPlugins();
        List<String> crawlerIdList = crawlers.stream().map(crawler -> crawler.getID().toString()).collect(Collectors.toList());
        if (!crawlersList.isEmpty() && !ListUtility.areListsEqual(crawlersList, crawlerIdList)) {
            throw new InvalidPluginListException("Server has changed. Please refresh your page");
        }
    }

    @SneakyThrows
    @Override
    public void checkConverterList(List<String> convertersList) {
        this.checkConverterPlugins();
        List<String> exporterIdList = exporters.stream().map(exporter -> exporter.getId().toString()).collect(Collectors.toList());
        if (!convertersList.isEmpty() && !ListUtility.areListsEqual(convertersList, exporterIdList)) {
            throw new InvalidPluginListException("Converter has changed. Please refresh your page");
        }
    }

}
