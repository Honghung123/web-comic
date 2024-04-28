package com.group17.comic.controller;

import com.group17.comic.dto.request.AlternatedChapterDTO;
import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.dto.response.SuccessfulResponse;
import com.group17.comic.model.*;
import com.group17.comic.service.PluginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; 
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
public class ComicController {
    private final PluginService pluginService;

    @GetMapping("/genres")
    @Operation(summary = "Get all genres", description = "Get all genres with specific server id. Default server id is 0 - plugin's index in plugin list Default the current page,offset, is 1 Default limit, a number of comics per page is 10")
    public SuccessfulResponse<List<Genre>> getGenres(
            @RequestParam(name = "server_id", defaultValue = "0") @PositiveOrZero int serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        var genres = pluginService.getAllGenres(serverId);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", genres);
    }

    @GetMapping("/lasted-comic")
    @Operation(summary = "Get lasted comics", description = "Get lasted comics with specific server id. Default server id is 0 - plugin's index in plugin list Default current page is 1")
    public SuccessfulResponse<List<ComicModel>> getNewestCommic(
            @RequestParam(name = "server_id", defaultValue = "0") @PositiveOrZero int serverId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int page, 
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        var dataDto = pluginService.getNewestCommic(serverId, page);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @GetMapping("/search")
    @Operation(summary = "Search comic", description = "Search comic base on keyword(by title, author or published year) or only genres with specific server id. Default server id is 0 - plugin's index in plugin list. Default the current page is 1.")
    public SuccessfulResponse<List<ComicModel>> searchComic(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "genres", defaultValue = "") String byGenres,
            @RequestParam(name = "authors", defaultValue = "") String byAuthorTagId,
            @RequestParam(name = "server_id", defaultValue = "0") @PositiveOrZero int serverId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int currentPage,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        var dataDto = pluginService.searchComic(serverId, keyword, byGenres, byAuthorTagId, currentPage);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData(),
                dataDto.getMeta());
    }

    @GetMapping("/reading/{tagId}")
    @Operation(summary = "Get comic infomation", description = "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<Comic> getComicInfo(
            @PathVariable(name = "tagId", required = true) String tagId,
            @RequestParam(name = "server_id", defaultValue = "0") @PositiveOrZero int serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        var comic = pluginService.getComicInfo(serverId, tagId);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", comic);
    }

    @GetMapping("/reading/{tagId}/chapters")
    @Operation(summary = "Get comic infomation", description = "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<List<Chapter>> getChapters(
            @PathVariable(name = "tagId", required = true) String tagId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int currentPage,
            @RequestParam(name = "server_id", defaultValue = "0") @PositiveOrZero int serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        var dataDto = pluginService.getChapters(serverId, tagId, currentPage);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @GetMapping("/reading/{tagId}/chapters/{chapter}")
    @Operation(summary = "Get comic infomation", description = "Get comic content of a chapter base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<ComicChapterContent> getComicChapterContent(
            @PathVariable(name = "tagId", required = true) String tagId,
            @PathVariable(name = "chapter", required = true) String currentChapter,
            @RequestParam(name = "server_id", defaultValue = "0") @PositiveOrZero int serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        var dataDto = pluginService.getComicChapterContent(serverId, tagId, currentChapter);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @PostMapping("/reading/change-server")
    @Operation(summary = "Change comic server", description = "Get comic content base of a chapter on alternate server with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<ComicChapterContent> getComicChapterContentOnOtherServer(
            @Valid @RequestBody AlternatedChapterDTO altChapterDto,
            @RequestParam(name = "server_id", defaultValue = "0") @PositiveOrZero int serverId,
            @RequestHeader(name = "crawler-size", defaultValue = "3") @PositiveOrZero int crawlerSize) {
        pluginService.checkCrawlerServerSize(crawlerSize);
        var dataDto = pluginService.getComicChapterContentOnOtherServer(serverId, altChapterDto);
        return new SuccessfulResponse<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @PostMapping("/export-file")
    @Operation(summary = "Export file", description = "Export a chapter content to a specific file. Default converter id is 0 - plugin's index in converter list.")
    public ResponseEntity<InputStreamResource> exportFileFromText(
            @Valid @RequestBody ChapterDTO chapterDto,
            @RequestParam(name = "converter_id", defaultValue = "0") @PositiveOrZero int converterId,
            @RequestHeader(name = "converter-size", defaultValue = "3") @PositiveOrZero int converterSize) {
        pluginService.checkConverterPluginSize(converterSize);
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
