package com.group17.comic.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.group17.comic.dto.request.AlternatedChapterDTO;
import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.dto.response.SuccessfulResponse;
import com.group17.comic.exception.customs.InvalidPluginListException;
import com.group17.comic.model.*;
import com.group17.comic.service.IComicService;
import com.group17.comic.service.IPluginService;

import com.group17.comic.utils.StringUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; 
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

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
    private final String DEFAULT_CRAWLER = "Tang Thu Vien";
    private final String DEFAULT_CONVERTER = "DOCX";

    @GetMapping("/genres")
    @Operation(summary = "Get all genres", description = "Get all genres with specific server id. Default server id is 0 - plugin's index in plugin list Default the current page,offset, is 1 Default limit, a number of comics per page is 10")
    public SuccessfulResponse<List<Genre>> getGenres(
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        if(serverId == null) {
            serverId = pluginService.getPluginIdByName(DEFAULT_CRAWLER);
        }
        var genres = comicService.getAllGenres(serverId);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", genres);
    }

    @GetMapping("/lasted-comic")
    @Operation(summary = "Get lasted comics", description = "Get lasted comics with specific server id. Default server id is 0 - plugin's index in plugin list Default current page is 1")
    public SuccessfulResponse<List<ComicModel>> getLatestComics(
            @RequestParam(name = "server_id", required = false)  UUID serverId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int page, 
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        if(serverId == null) {
            serverId = pluginService.getPluginIdByName(DEFAULT_CRAWLER);
        }

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
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        if(serverId == null) {
            serverId = pluginService.getPluginIdByName(DEFAULT_CRAWLER);
        }
        log.info("Server id is {}", serverId);
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
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        if(serverId == null) {
            serverId = pluginService.getPluginIdByName(DEFAULT_CRAWLER);
        }
        var dataDto = comicService.getComicsOfAnAuthor(serverId, authorId, page); 
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @GetMapping("/reading/{tagId}")
    @Operation(summary = "Get comic infomation", description = "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<Comic> getComicInfo(
            @PathVariable(name = "tagId", required = true) String tagId,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        if(serverId == null) {
            serverId = pluginService.getPluginIdByName(DEFAULT_CRAWLER);
        }
        var comic = comicService.getComicInfo(serverId, tagId);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", comic);
    }

    @GetMapping("/reading/{tagId}/chapters")
    @Operation(summary = "Get chapters of a comic", description = "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<List<Chapter>> getChapters(
            @PathVariable(name = "tagId", required = true) String tagId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int currentPage,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        if(serverId == null) {
            serverId = pluginService.getPluginIdByName(DEFAULT_CRAWLER);
        }
        var dataDto = comicService.getChapters(serverId, tagId, currentPage);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @PostMapping("/reading/change-server-comic-info")
    @Operation(summary = "Change comic info on other server", description = "Get comic content base of a chapter on alternate server with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<Comic> getComicInfoOnOtherServer(
            @Valid @RequestBody AlternatedChapterDTO altChapterDto,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        if(serverId == null) {
            serverId = pluginService.getPluginIdByName(DEFAULT_CRAWLER);
        }
        var comic = comicService.getComicInfoOnOtherServer(serverId, altChapterDto);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", comic);
    }

    @GetMapping("/reading/{tagId}/chapters/{chapter}")
    @Operation(summary = "Get comic chapter content", description = "Get comic content of a chapter base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<ComicChapterContent> getComicChapterContent(
            @PathVariable(name = "tagId", required = true) String tagId,
            @PathVariable(name = "chapter", required = true) String currentChapter,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        if(serverId == null) {
            serverId = pluginService.getPluginIdByName(DEFAULT_CRAWLER);
        }
        var dataDto = comicService.getComicChapterContent(serverId, tagId, currentChapter);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @PostMapping("/reading/change-server-chapter-content")
    @Operation(summary = "Change comic server", description = "Get comic content base of a chapter on alternate server with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<ComicChapterContent> getComicChapterContentOnOtherServer(
            @Valid @RequestBody AlternatedChapterDTO altChapterDto,
            @RequestParam(name = "server_id", defaultValue = "0")  UUID serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        if(serverId == null) {
            serverId = pluginService.getPluginIdByName(DEFAULT_CRAWLER);
        }
        var dataDto = comicService.getComicChapterContentOnOtherServer(serverId, altChapterDto);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @PostMapping("/export-file")
    @Operation(summary = "Export file", description = "Export a chapter content to a specific file. Default converter id is 0 - plugin's index in converter list.")
    public ResponseEntity<InputStreamResource> exportFileFromText(
            @Valid @RequestBody ChapterDTO chapterDto,
            @RequestParam(name = "converter_id", required = false) UUID converterId,
            @RequestHeader(name = "converter-size", defaultValue = "3") @PositiveOrZero int converterSize) {
        pluginService.checkConverterPluginSize(converterSize);
        if(converterId == null){
            converterId = pluginService.getPluginIdByName(DEFAULT_CONVERTER);
        }
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


}
