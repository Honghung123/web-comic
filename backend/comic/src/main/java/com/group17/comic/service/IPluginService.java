package com.group17.comic.service;

import java.util.List;
import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.enums.Plugin;
import com.group17.comic.model.ConverterPlugin;
import com.group17.comic.model.CrawlerPlugin;  

public interface IPluginService {
      public Object getPlugin(Plugin plugin, int pluginId);
      public void checkCrawlerPlugins();
      public List<ConverterPlugin> getAllConverterPlugins();
      public void checkConverterPluginSize(int convertersSize);
      public void checkCrawlerServerSize(int crawlersSize);
      public List<CrawlerPlugin> getAllCrawlerPlugins();
      public ChapterFile exportFile(ChapterDTO chapterDto, int converterId);
}
