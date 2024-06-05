package com.group17.comic;

import com.group17.comic.configurations.CommonConfiguration;
import com.group17.comic.configurations.Constants;
import com.group17.comic.model.CrawlerPlugin;
import com.group17.comic.service.implementations.CrawlerPluginServiceImpl;
import jakarta.annotation.PostConstruct;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group17.comic.controller.ComicController;
import com.group17.comic.dto.request.AlternatedChapterRequest;
import com.group17.comic.service.implementations.ComicServiceImpl;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(ComicController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = CommonConfiguration.class)
@RunWith(SpringRunner.class)
public class ComicControllerTests {
    @SpyBean
    private ComicServiceImpl comicService;
    @SpyBean
    private CrawlerPluginServiceImpl pluginService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        assertNotNull(comicService);
        assertNotNull(pluginService);
        assertNotNull(mockMvc);
        assertNotNull(objectMapper);
    }

    @Before
    public void beforeEachTests(){
//        pluginService.checkCrawlerPlugins();
        System.out.println("Crawler Plugins: " + pluginService.getAllPlugins().size());
    }


    private AlternatedChapterRequest mockAlternatedChapterDTO(String title, String authorName, String tagId, int chapterNumber) {
        return new AlternatedChapterRequest(title, authorName, tagId, chapterNumber);
    }

    private CrawlerPlugin mockCrawlerPlugin() {
        return new CrawlerPlugin(UUID.fromString("123e4567-e89b-12d3-a456-426614173000"), "string");
    }

    @Test
    public void testDependencyInjection() {
        assertThat(comicService).isNotNull();
        assertThat(pluginService).isNotNull();
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
    }

    @Test
    public void testValid_GetServerList_ReturnSuccess() throws Exception {
        String requestUrl = Constants.BASE_URL + "/converter-plugins";
        List<CrawlerPlugin> crawlerPlugins = new ArrayList<>();
        crawlerPlugins.add(mockCrawlerPlugin());
        Mockito.when(pluginService.getAllPlugins()).thenReturn(crawlerPlugins);
        mockMvc.perform(MockMvcRequestBuilders.get(requestUrl))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Ignore
    public void testValid_WhenAlteratedChapterDTOValid_ReturnSuccess() throws Exception {
        String requestUrl = Constants.BASE_URL + "/reading/change-server-chapter-content";
        var alterChapter = mockAlternatedChapterDTO("Tien Nghich", "Nhi Can", "tien-nghich", 11);
        var jsonObject = objectMapper.writeValueAsString(alterChapter);
        mockMvc.perform(MockMvcRequestBuilders.post(requestUrl)
                        .content(jsonObject)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("list_crawlers", objectMapper.writeValueAsString(Constants.SERVER_LIST))
                        .param("server_id", "123e4567-e89b-12d3-a456-426614173000")
                ).andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Ignore
    public void testValid_WhenServerListChange_ReturnFailed() throws Exception {
        String requestUrl = Constants.BASE_URL + "/reading/change-server-chapter-content";
        var alterChapter = mockAlternatedChapterDTO("Tien Nghich", "Nhi Can", "tien-nghich", 11);
        var jsonObject = objectMapper.writeValueAsString(alterChapter);
        var serverList = Arrays.copyOf(Constants.SERVER_LIST, 2) ;
        mockMvc.perform(MockMvcRequestBuilders.post(requestUrl)
                        .content(jsonObject)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("list_crawlers", objectMapper.writeValueAsString(serverList))
                        .param("server_id", "123e4567-e89b-12d3-a456-426614173000")
                )
                .andExpect(MockMvcResultMatchers.status().isExpectationFailed())
                .andDo(MockMvcResultHandlers.print());
    }
}
