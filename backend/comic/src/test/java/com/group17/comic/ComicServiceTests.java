package com.group17.comic;  

import com.group17.comic.service.IComicService;
import com.group17.comic.service.ICrawlerPluginService;
import com.group17.comic.service.IPluginService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ComicServiceTests {
	@Autowired
	@Qualifier("comicServiceV1")
	private IComicService comicService;

	@Autowired
	@Qualifier("crawlerPluginServiceV1")
	private ICrawlerPluginService pluginService;

	@PostConstruct
	public void init() throws IOException {
		pluginService.checkCurrentPlugins();
	}

//	public DataModel<Integer, List<Chapter>> getChaptersTest(int pluginId, String tagId, int currentPage) {
//		return comicService.getChapters(pluginId, tagId, currentPage);
//	}
//	public List<Genre> testGetAllGenre(int serverId){
//		return comicService.getAllGenres(serverId);
//	}
//
//	public DataModel<Integer, List<ComicModel>> getNewestComic(int pluginId, int page){
//		return comicService.getNewestCommic(pluginId, page);
//	}
//	public DataModel<Integer, List<ComicModel>> getComicsOfAnAuthor(int serverId, String authorId, int page){
//		return comicService.getComicsOfAnAuthor(serverId, authorId, page);
//	}
//	public DataSearchModel<Integer, List<ComicModel>, List<Author>> searchComic(int serverId,
//																				String keyword, String byGenres, int currentPage){
//		return comicService.searchComic(serverId, keyword, byGenres, currentPage);
//	}
//	public Comic getComicInfo(int pluginId, String tagUrl){
//		return comicService.getComicInfo(pluginId, tagUrl);
//	}
//	public DataModel<Integer, List<Chapter>> getChapters(int serverId, String tagId, int currentPage){
//		return comicService.getChapters(serverId, tagId, currentPage);
//	}
//	public Comic getComicInfoOnOtherServer(int serverId, AlternatedChapterDTO altChapterDto){
//		return comicService.getComicInfoOnOtherServer(serverId, altChapterDto);
//	}
//	public DataModel<?, ComicChapterContent> getComicChapterContent(int serverId,
//																	String tagId, String currentChapter){
//		return comicService.getComicChapterContent(serverId, tagId, currentChapter);
//	}
//	public DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(int serverId,
//																				 AlternatedChapterDTO altChapterDto){
//		return comicService.getComicChapterContentOnOtherServer(serverId, altChapterDto);
//	}

	// AE dinh nghia cac method can test o day, sau do, extend class de test cac method nay.
}
