package com.group17.comic.service.implementations;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.group17.comic.enums.ExceptionType;
import com.group17.comic.exceptions.BusinessException;
import com.group17.comic.models.*;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.service.ICrawlerPluginService;
import com.group17.comic.utils.ListUtility;
import com.group17.comic.utils.PluginUtility;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service("crawlerPluginServiceV1")
@Slf4j
public class CrawlerPluginServiceImpl implements ICrawlerPluginService {
    String baseDir = System.getProperty("user.dir");

    @Value("${comic.base_dir}")
    String projectDirectory;

    @Value("${comic.plugin.crawler.crawler_package_name}")
    String crawlerPackageName;

    @Value("${comic.plugin.crawler.crawler_directory}")
    String crawlerDirectory;

    private List<IDataCrawler> crawlers = new ArrayList<>();

    @Value("${comic.plugin.crawler.default_crawler_name}")
    private String DEFAULT_CRAWLER;

    @Override
    public Object getPluginById(UUID pluginId) {
        return crawlers.stream()
                .filter(crawler -> crawler.getID().equals(pluginId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionType.PLUGIN_NOT_FOUND));
    }

    @SneakyThrows
    @Override
    public void checkCurrentPlugins() {
        baseDir = PluginUtility.resolveAbsolutePath(System.getProperty("user.dir"));
        Path crawlerAbsolutePath = Paths.get(baseDir, projectDirectory, crawlerDirectory);
        var crawlerClasses = PluginUtility.getAllPluginsFromFolderWithoutInstantiation(
                crawlerAbsolutePath.toString(), crawlerPackageName, IDataCrawler.class);
        if (crawlers.isEmpty() || crawlerClasses.size() != crawlers.size()) {
            crawlers = PluginUtility.getAllPluginsFromFolder(
                    crawlerAbsolutePath.toString(), crawlerPackageName, IDataCrawler.class);
        }
    }

    @SneakyThrows
    @Override
    public List<CrawlerPlugin> getAllPlugins() {
        this.checkCurrentPlugins();
        List<CrawlerPlugin> pluginList = new ArrayList<>();
        for (var crawler : crawlers) {
            String pluginName = crawler.getPluginName();
            UUID id = crawler.getID();
            pluginList.add(new CrawlerPlugin(id, pluginName));
        }
        return pluginList;
    }

    @Override
    public UUID getDefaultPluginId() {
        return this.getPluginIdByName(DEFAULT_CRAWLER);
    }

    @Override
    public UUID getPluginIdByName(String name) {
        var crawlerPlugin = crawlers.stream()
                .filter(crawler -> crawler.getPluginName().equals(name))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionType.PLUGIN_NOT_FOUND));
        return crawlerPlugin.getID();
    }

    @SneakyThrows
    public void checkPluginList(List<String> pluginList) {
        this.checkCurrentPlugins();
        List<String> crawlerIdList =
                crawlers.stream().map(crawler -> crawler.getID().toString()).toList();
        if (!pluginList.isEmpty() && !ListUtility.areListsEqual(pluginList, crawlerIdList)) {
            throw new BusinessException(ExceptionType.PLUGIN_LIST_CHANGED);
        }
    }
}
