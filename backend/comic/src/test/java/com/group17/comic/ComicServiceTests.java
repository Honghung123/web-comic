package com.group17.comic;  

import com.group17.comic.model.Chapter;
import com.group17.comic.model.DataModel;
import com.group17.comic.service.IComicService;
import com.group17.comic.service.IPluginService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class ComicServiceTests {
	@Autowired
	@Qualifier("comicServiceV1")
	private IComicService comicService;

	@Autowired
	@Qualifier("pluginServiceV1")
	private IPluginService pluginService;

	@PostConstruct
	public void init() throws IOException {
		pluginService.checkCrawlerPlugins();
	}

	public DataModel<Integer, List<Chapter>> getChaptersTest(int pluginId, String tagId, int currentPage) {
		return comicService.getChapters(pluginId, tagId, currentPage);
	}

	// AE dinh nghia cac method can test o day, sau do, extend class de test cac method nay.
}
