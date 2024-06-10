package com.group17.comic;  

import com.group17.comic.dtos.request.AlternatedChapterRequest;
import com.group17.comic.dtos.response.AuthorResponse;
import com.group17.comic.enums.PluginServiceType;
import com.group17.comic.factories.PluginFactory;
import com.group17.comic.models.*;
import com.group17.comic.service.IComicService;
import com.group17.comic.service.IPluginServiceProvider;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ComicServiceTests {
	@Autowired
	@Qualifier("comicServiceV1")
	private IComicService comicService;

	@Autowired
	private IPluginServiceProvider pluginServiceProvider;

	@Autowired
	private PluginFactory pluginFactory;

	@PostConstruct
	public void init() throws IOException {
		pluginServiceProvider.getPluginServiceByType(PluginServiceType.CRAWLER_SERVICE).checkCurrentPlugins();
		pluginServiceProvider.getPluginServiceByType(PluginServiceType.EXPORTER_SERVICE).checkCurrentPlugins();
	}

	public PageableData<Integer, List<Chapter>> getChaptersTest(UUID pluginId, String tagId, int currentPage) {
		return comicService.getChapters(pluginId, tagId, currentPage);
	}
	public List<Genre> testGetAllGenre(UUID serverId){
		return comicService.getAllGenres(serverId);
	}

	public PageableData<Integer, List<LatestComic>> getNewestComic(UUID pluginId, int page){
		return comicService.getNewestCommic(pluginId, page);
	}
	public PageableData<Integer, List<LatestComic>> getComicsOfAnAuthor(UUID serverId, String authorId, String tagId, int page){
		return comicService.getComicsOfAnAuthor(serverId, authorId, tagId, page);
	}
	public SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> searchComic(UUID serverId,
																							   String keyword, String byGenres, int currentPage){
		return comicService.searchComic(serverId, keyword, byGenres, currentPage);
	}
	public Comic getComicInfo(UUID pluginId, String tagUrl){
		return comicService.getComicInfo(pluginId, tagUrl);
	}
	public PageableData<Integer, List<Chapter>> getChapters(UUID serverId, String tagId, int currentPage){
		return comicService.getChapters(serverId, tagId, currentPage);
	}
	public Comic getComicInfoOnOtherServer(UUID serverId, AlternatedChapterRequest altChapterDto){
		return comicService.getComicInfoOnOtherServer(serverId, altChapterDto);
	}
	public PageableData<?, ComicChapterContent> getComicChapterContent(UUID serverId,
																	   String tagId, String currentChapter){
		return comicService.getComicChapterContent(serverId, tagId, currentChapter);
	}
	public PageableData<?, ComicChapterContent> getComicChapterContentOnOtherServer(UUID serverId,
																					AlternatedChapterRequest altChapterDto){
		return comicService.getComicChapterContentOnOtherServer(serverId, altChapterDto);
	}

	@Test
	void demo(){
		assertThat(comicService).isNotNull();
		assertThat(pluginServiceProvider).isNotNull();
	}
}
