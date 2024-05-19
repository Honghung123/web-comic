package com.group17.comic.service;

import java.util.List;
import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.enums.Plugin;
import com.group17.comic.model.ConverterPlugin;
import com.group17.comic.model.CrawlerPlugin;  

public interface IPluginService {
      // public List<Genre> getAllGenres(int pluginId);
      // public DataModel<Integer, List<ComicModel>> getNewestCommic(int pluginId, int page);
      // public DataModel<Integer, List<ComicModel>> getComicsOfAnAuthor(int serverId, String authorId, int page);
      // public DataSearchModel<Integer, List<ComicModel>, List<Author>> searchComic(int serverId,
      //                                                 String keyword, String byGenres, int currentPage);
      // public Comic getComicInfo(int pluginId, String tagUrl);
      // public DataModel<Integer, List<Chapter>> getChapters(int serverId, String tagId, int currentPage);
      // public Comic getComicInfoOnOtherServer(int serverId, AlternatedChapterDTO altChapterDto);
      // public DataModel<?, ComicChapterContent> getComicChapterContent(int serverId,
      //                                                 String tagId, String currentChapter);
      // public DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(int serverId,
      //                                                 AlternatedChapterDTO altChapterDto); 

      public Object getPlugin(Plugin plugin, int pluginId);
      public void checkCrawlerPlugins();
      public List<ConverterPlugin> getAllConverterPlugins();
      public void checkConverterPluginSize(int convertersSize);
      public void checkCrawlerServerSize(int crawlersSize);
      public List<CrawlerPlugin> getAllCrawlerPlugins();
      public ChapterFile exportFile(ChapterDTO chapterDto, int converterId);
}
