package com.group17.comic.controllers;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group17.comic.dtos.request.AlternatedChapterRequest;
import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.dtos.response.SuccessfulResponse;
import com.group17.comic.models.*;
import com.group17.comic.service.*;
import com.group17.comic.utils.StringUtility;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/comic")
@RequiredArgsConstructor
@Tag(name = "Comic Controller")
@Validated
public class ComicController {
    @Qualifier("comicServiceV1")
    private final IComicService comicService;

    @Qualifier("crawlerPluginServiceV1")
    private final ICrawlerPluginService crawlerPluginService;

    @Qualifier("exporterPluginServiceV1")
    private final IExporterPluginService exporterPluginService;

    @GetMapping("/genres")
    @Operation(
            summary = "Get all genres",
            description =
                    "Get all genres with specific server id. Default server id is 0 - plugin's index in plugin list Default the current page,offset, is 1 Default limit, a number of comics per page is 10")
    public SuccessfulResponse<List<Genre>> getGenres(
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list_crawlers", required = true) String crawlers) {
        crawlerPluginService.checkPluginList(StringUtility.getArrayFromJSON(crawlers));
        var genres = comicService.getAllGenres(serverId);
        return new SuccessfulResponse<>(HttpStatus.OK, "Get all genres successfully", genres);
    }

    @GetMapping("/lasted-comic")
    @Operation(
            summary = "Get lasted comics",
            description =
                    "Get lasted comics with specific server id. Default server id is 0 - plugin's index in plugin list Default current page is 1")
    public SuccessfulResponse<List<LatestComic>> getLatestComics(
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int page,
            @RequestHeader(name = "list_crawlers", required = true) String crawlers) {
        crawlerPluginService.checkPluginList(StringUtility.getArrayFromJSON(crawlers));
        var dataDto = comicService.getNewestCommic(serverId, page);
        return new SuccessfulResponse<>(
                HttpStatus.OK, "Get lasted comic successfully", dataDto.getPagination(), dataDto.getData());
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search comic",
            description =
                    "Search comic base on keyword(by title, author or published year) or only genres with specific server id. Default server id is 0 - plugin's index in plugin list. Default the current page is 1.")
    public SuccessfulResponse<List<LatestComic>> searchComic(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "genre", defaultValue = "") String byGenre,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int currentPage,
            @RequestHeader(name = "list_crawlers", required = true) String crawlers) {
        crawlerPluginService.checkPluginList(StringUtility.getArrayFromJSON(crawlers));
        var dataDto = comicService.searchComic(serverId, keyword, byGenre, currentPage);
        return new SuccessfulResponse<>(
                HttpStatus.OK,
                "Search comic successfully",
                dataDto.getPagination(),
                dataDto.getData(),
                dataDto.getMeta());
    }

    @GetMapping("/author/{authorId}")
    @Operation(
            summary = "Get author's comics",
            description =
                    "Get author's comics base on author id with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<List<LatestComic>> getComicsOfAnAuthor(
            @PathVariable(name = "authorId", required = true) String authorId,
            @RequestParam(name = "tagId", required = true) @NotBlank String tagId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int page,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list_crawlers", required = true) String crawlers) {
        crawlerPluginService.checkPluginList(StringUtility.getArrayFromJSON(crawlers));
        var dataDto = comicService.getComicsOfAnAuthor(serverId, authorId, tagId, page);
        return new SuccessfulResponse<>(
                HttpStatus.OK, "Get comics of an author successfully", dataDto.getPagination(), dataDto.getData());
    }

    @GetMapping("/reading/{tagId}")
    @Operation(
            summary = "Get comic infomation",
            description =
                    "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<Comic> getComicInfo(
            @PathVariable(name = "tagId", required = true) String tagId,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list_crawlers", required = true) String crawlers) {
        crawlerPluginService.checkPluginList(StringUtility.getArrayFromJSON(crawlers));
        var comic = comicService.getComicInfo(serverId, tagId);
        return new SuccessfulResponse<>(HttpStatus.OK, "Get comic infomation successfully", comic);
    }

    @GetMapping("/reading/{tagId}/chapters")
    @Operation(
            summary = "Get chapters of a comic",
            description =
                    "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<List<Chapter>> getChapters(
            @PathVariable(name = "tagId", required = true) String tagId,
            @RequestParam(name = "page", defaultValue = "1") @Positive int currentPage,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list_crawlers", required = true) String crawlers) {
        crawlerPluginService.checkPluginList(StringUtility.getArrayFromJSON(crawlers));
        var dataDto = comicService.getChapters(serverId, tagId, currentPage);
        return new SuccessfulResponse<>(
                HttpStatus.OK, "Get chapters successfully", dataDto.getPagination(), dataDto.getData());
    }

    @PostMapping("/reading/change-server-comic-info")
    @Operation(
            summary = "Change comic info on other server",
            description =
                    "Get comic content base of a chapter on alternate server with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<Comic> getComicInfoOnOtherServer(
            @Valid @RequestBody AlternatedChapterRequest altChapterDto,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list_crawlers", required = true) String crawlers) {
        crawlerPluginService.checkPluginList(StringUtility.getArrayFromJSON(crawlers));
        var comic = comicService.getComicInfoOnOtherServer(serverId, altChapterDto);
        return new SuccessfulResponse<>(HttpStatus.OK, "Change server for comic info successfully", comic);
    }

    @GetMapping("/reading/{tagId}/chapters/{chapter}")
    @Operation(
            summary = "Get comic chapter content",
            description =
                    "Get comic content of a chapter base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<ComicChapterContent> getComicChapterContent(
            @PathVariable(name = "tagId", required = true) String tagId,
            @PathVariable(name = "chapter", required = true) String currentChapter,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list_crawlers", required = true) String crawlers) {
        crawlerPluginService.checkPluginList(StringUtility.getArrayFromJSON(crawlers));
        var dataDto = comicService.getComicChapterContent(serverId, tagId, currentChapter);
        return new SuccessfulResponse<>(
                HttpStatus.OK, "Get comic chapter content successfully", dataDto.getPagination(), dataDto.getData());
    }

    @PostMapping("/reading/change-server-chapter-content")
    @Operation(
            summary = "Change comic server",
            description =
                    "Get comic content base of a chapter on alternate server with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public SuccessfulResponse<ComicChapterContent> getComicChapterContentOnOtherServer(
            @Valid @RequestBody AlternatedChapterRequest altChapterDto,
            @RequestParam(name = "server_id", required = false) UUID serverId,
            @RequestHeader(name = "list_crawlers", required = true) String crawlers) {
        crawlerPluginService.checkPluginList(StringUtility.getArrayFromJSON(crawlers));
        var dataDto = comicService.getComicChapterContentOnOtherServer(serverId, altChapterDto);
        return new SuccessfulResponse<>(
                HttpStatus.OK,
                "Change server for comic chapter content successfully",
                dataDto.getPagination(),
                dataDto.getData());
    }

    @PostMapping("/export-file")
    @Operation(
            summary = "Export file",
            description =
                    "Export a chapter content to a specific file. Default converter id is 0 - plugin's index in converter list.")
    public ResponseEntity<InputStreamResource> exportFileFromText(
            @Valid @RequestBody ChapterRequest chapterDto,
            @RequestParam(name = "converter_id", required = false) UUID exporterId,
            @RequestHeader(name = "list_exporters", required = true) String exporters) {
        exporterPluginService.checkPluginList(StringUtility.getArrayFromJSON(exporters));
        ChapterFile chapterFile = exporterPluginService.exportFile(chapterDto, exporterId);
        return new ResponseEntity<>(chapterFile.getResource(), chapterFile.getHeaders(), HttpStatus.OK);
    }

    @GetMapping("/crawler-plugins")
    @Operation(
            summary = "Get all crawler plugins",
            description =
                    "Get all crawler plugins from the folder to crawl comic. Id of each plugin is the index of it in plugin list")
    public SuccessfulResponse<List<CrawlerPlugin>> getAllCrawlerPlugins() {
        var crawlers = crawlerPluginService.getAllPlugins();
        return new SuccessfulResponse<>(HttpStatus.OK, "Get all crawler servers successfully", crawlers);
    }

    @GetMapping("/converter-plugins")
    @Operation(
            summary = "Get all converter plugins",
            description =
                    "Get all converter plugins from the folder to crawl comic. Id of each plugin is the index of it in plugin list")
    public SuccessfulResponse<List<ConverterPlugin>> getAllConverterPlugins() {
        var converters = exporterPluginService.getAllPlugins();
        return new SuccessfulResponse<>(HttpStatus.OK, "Get all converter servers successfully", converters);
    }
}
