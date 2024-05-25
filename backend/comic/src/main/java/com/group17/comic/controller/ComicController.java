package com.group17.comic.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group17.comic.dto.request.AlternatedChapterDTO;
import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.dto.response.SuccessfulResponse; 
import com.group17.comic.model.*;
import com.group17.comic.service.IComicService;
import com.group17.comic.service.IPluginService;
import com.group17.comic.utils.StringUtility;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; 
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader; 

// @Tag: A Swagger annotation used to categorize API endpoints related to the Plugin Controller.
// @SneakyThrows: A Lombok annotation to silently throw checked exceptions
// @Operation: A Swagger annotation to describe an HTTP operation on an API endpoint

@RestController
@RequestMapping("/api/v1/comic")
@RequiredArgsConstructor
@Tag(name = "Comic Controller")
@Validated
@Slf4j
public class ComicController {
    @Qualifier("pluginServiceV1")
    private final IPluginService pluginService;
    @Qualifier("comicServiceV1")
    private final IComicService comicService;
    @Value("${comic.plugin.crawler.default_crawler_name}")
    private String DEFAULT_CRAWLER;
    @Value("${comic.plugin.converter.default_converter_name}")
    private String DEFAULT_CONVERTER; 

    private UUID getDefaultPluginIdByNameIfNull(UUID serverId, String pluginName) {
        if(serverId == null) {
            return pluginService.getPluginIdByName(pluginName);
        }
        return serverId;
    }

    @GetMapping("/genres")
    @Operation(summary = "Get all genres", description = "Get all genres with specific server id. Default server id is 0 - plugin's index in plugin list Default the current page,offset, is 1 Default limit, a number of comics per page is 10")
    public SuccessfulResponse<List<Genre>> getGenres(
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list-crawlers", defaultValue = "") String crawlers) {
        List<String> crawlerList = StringUtility.getArrayFromJSON(crawlers);
        pluginService.checkCrawlerList(crawlerList);
        serverId = this.getDefaultPluginIdByNameIfNull(serverId, DEFAULT_CRAWLER);
        var genres = comicService.getAllGenres(serverId);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", genres);
    }

    @GetMapping("/lasted-comic")
    @Operation(summary = "Get lasted comics", description = "Get lasted comics with specific server id. Default server id is 0 - plugin's index in plugin list Default current page is 1")
    public SuccessfulResponse<List<ComicModel>> getLatestComics(
            @RequestParam(name = "server_id", required = false)  UUID serverId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int page, 
            @RequestHeader(name = "list-crawlers", defaultValue = "") String crawlers) {
        List<String> crawlerList = StringUtility.getArrayFromJSON(crawlers);
        pluginService.checkCrawlerList(crawlerList);
        serverId = this.getDefaultPluginIdByNameIfNull(serverId, DEFAULT_CRAWLER);
        var dataDto = comicService.getNewestCommic(serverId, page);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @GetMapping("/search")
    @Operation(summary = "Search comic", description = "Search comic base on keyword(by title, author or published year) or only genres with specific server id. Default server id is 0 - plugin's index in plugin list. Default the current page is 1.")
    public SuccessfulResponse<List<ComicModel>> searchComic(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "genre", defaultValue = "") String byGenre, 
            @RequestParam(name = "server_id", required = false)UUID serverId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int currentPage,
            @RequestHeader(name = "list-crawlers", defaultValue = "") String crawlers) {
        List<String> crawlerList = StringUtility.getArrayFromJSON(crawlers);
        pluginService.checkCrawlerList(crawlerList);
        serverId = this.getDefaultPluginIdByNameIfNull(serverId, DEFAULT_CRAWLER);
        var dataDto = comicService.searchComic(serverId, keyword, byGenre, currentPage);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData(),
                dataDto.getMeta());
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get author's comics", description = "Get author's comics base on author id with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<List<ComicModel>> getComicsOfAnAuthor(
            @PathVariable(name = "authorId", required = true) String authorId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int page,
            @RequestParam(name = "server_id", required = false)  UUID serverId,
            @RequestHeader(name = "list-crawlers", defaultValue = "") String crawlers) {
        List<String> crawlerList = StringUtility.getArrayFromJSON(crawlers);
        pluginService.checkCrawlerList(crawlerList);
        serverId = this.getDefaultPluginIdByNameIfNull(serverId, DEFAULT_CRAWLER);
        var dataDto = comicService.getComicsOfAnAuthor(serverId, authorId, page); 
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @GetMapping("/reading/{tagId}")
    @Operation(summary = "Get comic infomation", description = "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<Comic> getComicInfo(
            @PathVariable(name = "tagId", required = true) String tagId,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list-crawlers", defaultValue = "") String crawlers) {
        List<String> crawlerList = StringUtility.getArrayFromJSON(crawlers);
        pluginService.checkCrawlerList(crawlerList);
        serverId = this.getDefaultPluginIdByNameIfNull(serverId, DEFAULT_CRAWLER);
        var comic = comicService.getComicInfo(serverId, tagId);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", comic);
    }

    @GetMapping("/reading/{tagId}/chapters")
    @Operation(summary = "Get chapters of a comic", description = "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<List<Chapter>> getChapters(
            @PathVariable(name = "tagId", required = true) String tagId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int currentPage,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list-crawlers", defaultValue = "") String crawlers) {
        List<String> crawlerList = StringUtility.getArrayFromJSON(crawlers);
        pluginService.checkCrawlerList(crawlerList);
        serverId = this.getDefaultPluginIdByNameIfNull(serverId, DEFAULT_CRAWLER);
        var dataDto = comicService.getChapters(serverId, tagId, currentPage);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @PostMapping("/reading/change-server-comic-info")
    @Operation(summary = "Change comic info on other server", description = "Get comic content base of a chapter on alternate server with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<Comic> getComicInfoOnOtherServer(
            @Valid @RequestBody AlternatedChapterDTO altChapterDto,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list-crawlers", defaultValue = "") String crawlers) {
        List<String> crawlerList = StringUtility.getArrayFromJSON(crawlers);
        pluginService.checkCrawlerList(crawlerList);
        serverId = this.getDefaultPluginIdByNameIfNull(serverId, DEFAULT_CRAWLER);
        var comic = comicService.getComicInfoOnOtherServer(serverId, altChapterDto);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", comic);
    }

    @GetMapping("/reading/{tagId}/chapters/{chapter}")
    @Operation(summary = "Get comic chapter content", description = "Get comic content of a chapter base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<ComicChapterContent> getComicChapterContent(
            @PathVariable(name = "tagId", required = true) String tagId,
            @PathVariable(name = "chapter", required = true) String currentChapter,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list-crawlers", defaultValue = "") String crawlers) {
        List<String> crawlerList = StringUtility.getArrayFromJSON(crawlers);
        pluginService.checkCrawlerList(crawlerList);
        serverId = this.getDefaultPluginIdByNameIfNull(serverId, DEFAULT_CRAWLER);
        var dataDto = comicService.getComicChapterContent(serverId, tagId, currentChapter);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @PostMapping("/reading/change-server-chapter-content")
    @Operation(summary = "Change comic server", description = "Get comic content base of a chapter on alternate server with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<ComicChapterContent> getComicChapterContentOnOtherServer(
            @Valid @RequestBody AlternatedChapterDTO altChapterDto,
            @RequestParam(name = "server_id", required = false)  UUID serverId,
            @RequestHeader(name = "list-crawlers", defaultValue = "") String crawlers) {
        List<String> crawlerList = StringUtility.getArrayFromJSON(crawlers);
        pluginService.checkCrawlerList(crawlerList);
        serverId = this.getDefaultPluginIdByNameIfNull(serverId, DEFAULT_CRAWLER);
        var dataDto = comicService.getComicChapterContentOnOtherServer(serverId, altChapterDto);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @PostMapping("/export-file")
    @Operation(summary = "Export file", description = "Export a chapter content to a specific file. Default converter id is 0 - plugin's index in converter list.")
    public ResponseEntity<InputStreamResource> exportFileFromText(
            @Valid @RequestBody ChapterDTO chapterDto,
            @RequestParam(name = "converter_id", required = false) UUID converterId,
            @RequestHeader(name = "list-converters", defaultValue = "") String converters) {
        List<String> converterList = StringUtility.getArrayFromJSON(converters);
        pluginService.checkConverterList(converterList);
        converterId = this.getDefaultPluginIdByNameIfNull(converterId, DEFAULT_CONVERTER);
        ChapterFile chapterFile = pluginService.exportFile(chapterDto, converterId);
        return new ResponseEntity<>(chapterFile.getResource(), chapterFile.getHeaders(), HttpStatus.OK);
    }

    @GetMapping("/crawler-plugins")
    @Operation(summary = "Get all crawler plugins", description = "Get all crawler plugins from the folder to crawl comic. Id of each plugin is the index of it in plugin list")
    public SuccessfulResponse<List<CrawlerPlugin>> getAllPCrawlerlugins() {
        var crawlers = pluginService.getAllCrawlerPlugins();
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", crawlers);
    }

    @GetMapping("/converter-plugins")
    @Operation(summary = "Get all converter plugins", description = "Get all converter plugins from the folder to crawl comic. Id of each plugin is the index of it in plugin list")
    public SuccessfulResponse<List<ConverterPlugin>> getAllPConverterlugins() {
        var converters = pluginService.getAllConverterPlugins();
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", converters);
    }
    @GetMapping("testing")
    public ResponseEntity<?> testMobi() throws IOException {
        String content = "This";
        String fileOutputName = "sdfadsf.txt";
        final String API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiN2I3NGUyZjc3NTI0OTYwNzA0MGMxMWQ4MTIwNTZhNTIyMWY1NDkyNTEyOWYzMzQ2NTBiYWRiZGNhZjNjOGYwYTg2ZGEyM2Y1ZGNhMGFlZTkiLCJpYXQiOjE3MTY1NjMzOTIuMjEzNjA1LCJuYmYiOjE3MTY1NjMzOTIuMjEzNjA3LCJleHAiOjQ4NzIyMzY5OTIuMjA5ODU1LCJzdWIiOiI2ODQ4MDIwNiIsInNjb3BlcyI6WyJ1c2VyLnJlYWQiLCJ1c2VyLndyaXRlIiwidGFzay5yZWFkIiwidGFzay53cml0ZSIsIndlYmhvb2sucmVhZCIsIndlYmhvb2sud3JpdGUiLCJwcmVzZXQucmVhZCIsInByZXNldC53cml0ZSJdfQ.d8xv5FPsXoKxp7NoGe_IyqzwUhjraQNwIR07cioygczVJU2SV8UGksvKum4ljdzYaI7QaxEeOw4Ji91lR2wPgcCZcwadMYZieau1je-vzmY30U3Y2a3s0VjOokCUEvoXtciyFJZviO9zaWtv7-hHRDMOxYczw4EuNChs0RMPcBsBJJBRAgC9LN-npsViOsDlPvzvQTWZrtCZFWdIgOKJoL87M44J8mTKhFBWP2Ixw8nxkywsfm_cDHU7TPpudaDbdbe_cVVxN4V_VbAkYUwisR8LM7NhqvrzBnGK_ZNbVOYdGAnreDs4fyS1APWcXWyVsDP4cYhyOwwVodKOiBZhbfsAcLyFh2GHt1kC3e5ic44Dm6LxNTKS6sA-r1IsOxg8wGBm_zH-txo6PEK6c_ySi-ny9dZb5Qdit0fetASzzjkjjhjHgII-1JQkNj5SktkvSsoKxXJDrAT-Wmo8srybG_34n_rqAO9vI2rMQwbgg7_56RGoIAyeakKAW15RoF7Xy3SvC1kOl7x4AQ7tsSARgtSYhEI7j00zk9RgHiL_VkW9Ao9OCmrMZcrU1Z2L02kVtwOpdcPcRqlJBb8lA4-siGmt2OcAf2_z8rVGVFZIvE9GtwYnA1Ud70HQnG5YNFPgAuMF3F9scmZAdqrtPaqWvidy6RtG6dPECu7LLq1vgOw";
        final String BASE_URL = "https://api.cloudconvert.com/v2";
        String url = BASE_URL + "/jobs";
        JsonObject requestJson = new JsonObject();
        JsonObject tasks = new JsonObject();
        JsonObject importFile = new JsonObject();
        importFile.addProperty("operation", "import/raw");

        importFile.addProperty("file", content);
        importFile.addProperty("filename", fileOutputName);
        tasks.add("import-file", importFile);

        JsonObject convertFile = new JsonObject();
        convertFile.addProperty("operation", "convert");
        convertFile.addProperty("input", "import-file");
        convertFile.addProperty("input_format", "txt");
        convertFile.addProperty("output_format", "mobi");
        convertFile.addProperty("page_range", "1-2");
        convertFile.addProperty("optimize_print", true);
        tasks.add("convert-file", convertFile);

        JsonObject exportFile = new JsonObject();
        exportFile.addProperty("operation", "export/url");
        exportFile.addProperty("input", "convert-file");
        tasks.add("export-file", exportFile);

        JsonObject requestBody = new JsonObject();
        requestBody.add("tasks", tasks);


        okhttp3.RequestBody body = okhttp3.RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        OkHttpClient webClient = new OkHttpClient();
        Response response = webClient.newCall(request).execute();
        if(response.code() == 201){
            JsonObject responseBody = new JsonParser().parse(response.body().string()).getAsJsonObject();
            JsonObject data = responseBody.get("data").getAsJsonObject();
            String id = data.get("id").getAsString();
            log.info("Id of the job" + id);
            request = new Request.Builder()
                    .url("https://sync.api.cloudconvert.com/v2/jobs/" + id)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .get()
                    .build();

            response = webClient.newCall(request).execute();
            String downloadUrl = "";
            if(response.code() == 200){
                responseBody = new JsonParser().parse(response.body().string()).getAsJsonObject();
                data = responseBody.get("data").getAsJsonObject();
                JsonArray tasksJson = data.getAsJsonArray("tasks");
                for (JsonElement taskElem : tasksJson) {
                    JsonObject task = taskElem.getAsJsonObject();
                    // We only want to process the "export-file" task.
                    if (task.get("name").getAsString().equals("export-file")) {

                        if (task.has("result")) {
                            JsonObject result = task.getAsJsonObject("result");

                            if (result.has("files")) {
                                JsonArray files = result.getAsJsonArray("files");

                                // Assuming there's only one file per task.
                                JsonObject file = files.get(0).getAsJsonObject();

                                if (file.has("url")) {
                                    downloadUrl = file.get("url").getAsString();

                                }
                            }
                        }
                    }
                }
            }
            request = new Request.Builder()
                    .url(downloadUrl)
                    .build();
            response = webClient.newCall(request).execute();
            byte[] fileBytes = response.body().bytes();

        }

        return ResponseEntity.status(response.code()).body(response.code() + requestBody.toString());
    }
}
