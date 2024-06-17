package com.group17.comic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group17.comic.configurations.CommonConfiguration;
import com.group17.comic.configurations.Constants;
import com.group17.comic.controllers.ComicController;
import com.group17.comic.dtos.request.AlternatedChapterRequest;
import com.group17.comic.exceptions.BusinessException;
import com.group17.comic.models.CrawlerPlugin;
import com.group17.comic.service.implementations.ComicServiceImpl;
import com.group17.comic.service.implementations.CrawlerPluginServiceImpl;
import com.group17.comic.service.implementations.ExporterPluginServiceImpl;

@WebMvcTest(ComicController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = CommonConfiguration.class)
@ExtendWith(SpringExtension.class)
class ComicControllerTests {
    @SpyBean
    private ComicServiceImpl comicService;

    @SpyBean
    private CrawlerPluginServiceImpl crawlerService;

    @SpyBean
    private ExporterPluginServiceImpl exporterService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    void init() {
        assertNotNull(comicService);
        assertNotNull(mockMvc);
        assertNotNull(objectMapper);
    }

    private AlternatedChapterRequest mockAlternatedChapterDTO(
            String title, String authorName, String tagId, int chapterNumber) {
        return new AlternatedChapterRequest(title, authorName, tagId, chapterNumber);
    }

    private CrawlerPlugin mockCrawlerPlugin() {
        return new CrawlerPlugin(UUID.fromString("123e4567-e89b-12d3-a456-426614173000"), "Tang Thu Vien");
    }

    @Test
    void testDependencyInjection() {
        assertThat(comicService).isNotNull();
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
    }

    @Test
    void testValid_GetServerList_ReturnSuccess() throws Exception {
        String requestUrl = Constants.BASE_URL + "/converter-plugins";
        //        List<CrawlerPlugin> crawlerPlugins = new ArrayList<>();
        //        crawlerPlugins.add(mockCrawlerPlugin());
        //        var exporterService = getPluginServiceByType(PluginServiceType.EXPORTER_SERVICE);
        //        Mockito.when(exporterService.getAllPlugins()).thenReturn(crawlerPlugins);
        mockMvc.perform(MockMvcRequestBuilders.get(requestUrl))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testValid_WhenAlteratedChapterDTOValid_ReturnSuccess() throws Exception {
        String requestUrl = Constants.BASE_URL + "/reading/change-server-chapter-content";
        var alterChapter = mockAlternatedChapterDTO("Tien Nghich", "Nhi Can", "tien-nghich", 11);
        var jsonObject = objectMapper.writeValueAsString(alterChapter);
        mockMvc.perform(MockMvcRequestBuilders.post(requestUrl)
                        .content(jsonObject)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("list_crawlers", objectMapper.writeValueAsString(Constants.SERVER_LIST))
                        .param("server_id", "123e4567-e89b-12d3-a456-426614173000"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testValid_WhenServerListChange_ReturnFailed() throws Exception {
        String requestUrl = Constants.BASE_URL + "/reading/change-server-chapter-content";
        var alterChapter = mockAlternatedChapterDTO("Tien Nghich", "Nhi Can", "tien-nghich", 11);
        var jsonObject = objectMapper.writeValueAsString(alterChapter);
        var serverList = Arrays.copyOf(Constants.SERVER_LIST, 2);
        try {
            mockMvc.perform(MockMvcRequestBuilders.post(requestUrl)
                            .content(jsonObject)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("list_crawlers", objectMapper.writeValueAsString(serverList))
                            .param("server_id", "123e4567-e89b-12d3-a456-426614173000"))
                    .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                    .andDo(MockMvcResultHandlers.print());
        } catch (Exception ex) {
            assertInstanceOf(RuntimeException.class, ex.getCause());
            var cause = (BusinessException) ex.getCause();
            assertInstanceOf(BusinessException.class, cause);
            assertEquals(HttpStatus.BAD_REQUEST, cause.getStatus());
        }
    }
}
