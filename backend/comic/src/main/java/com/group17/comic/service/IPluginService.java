package com.group17.comic.service;

import java.util.List;
import java.util.UUID;

import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.enums.Plugin;
import com.group17.comic.model.ConverterPlugin;
import com.group17.comic.model.CrawlerPlugin;  

public interface IPluginService {
       Object getPlugin(Plugin plugin, UUID pluginId);
       void checkCrawlerPlugins();
       List<ConverterPlugin> getAllConverterPlugins();
       void checkConverterPluginSize(int convertersSize);
       void checkCrawlerServerSize(int crawlersSize);
       List<CrawlerPlugin> getAllCrawlerPlugins();
       ChapterFile exportFile(ChapterDTO chapterDto, UUID converterId);
       UUID getPluginIdByName(String name);
}
