package com.group17.comic.controller;

import com.group17.comic.dto.response.ResponseSuccess;
import com.group17.comic.model.*; 
import com.group17.comic.service.PluginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag; 
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.http.HttpStatus; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @Tag: A Swagger annotation used to categorize API endpoints related to the Plugin Controller.
// @SneakyThrows: A Lombok annotation to silently throw checked exceptions
// @Operation: A Swagger annotation to describe an HTTP operation on an API endpoint

@RestController
@RequestMapping("/api/v1/comic")
@RequiredArgsConstructor
@Tag(name = "Plugin Controller")
public class PluginController {
    private final PluginService pluginService;

    @GetMapping("/search")
    @SneakyThrows   
    @Operation(summary = "Search comic", description = "Search comic base on keyword with specific server id. Default server id is 0 - plugin's index in plugin list. Default the current page is 1.")
    public ResponseSuccess<List<ComicModel>> searchComic(
                    @RequestParam(name = "keyword", required = true) String keyword, 
                    @RequestParam(name = "server_id", defaultValue = "0") int serverId,
                    @RequestParam(name = "page", defaultValue = "1") int currentPage
        ){ 
        var dataDto = pluginService.searchComic(serverId, keyword, currentPage);
        return new ResponseSuccess<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData(), dataDto.getMeta());
    }

    @GetMapping("/reading/{tagId}")
    @SneakyThrows   
    @Operation(summary = "Get comic infomation", description = "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public ResponseSuccess<Comic> getComicInfo(
                    @PathVariable(name = "tagId", required = true) String tagId, 
                    @RequestParam(name = "server_id", defaultValue = "0") int serverId
        ){ 
        var comic = pluginService.getComicInfo(serverId, tagId);
        return new ResponseSuccess<>(HttpStatus.OK, "Success", comic);
    }
    
    @GetMapping("/reading/{tagId}/chapters")
    @SneakyThrows   
    @Operation(summary = "Get comic infomation", description = "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public ResponseSuccess<List<Chapter>> getChapters(
                    @PathVariable(name = "tagId", required = true) String tagId, 
                    @RequestParam(name = "page", defaultValue = "1") int currentPage,
                    @RequestParam(name = "server_id", defaultValue = "0") int serverId
        ){ 
        var dataDto = pluginService.getChapters(serverId, tagId, currentPage);
        return new ResponseSuccess<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }
    
    @GetMapping("/reading/{tagId}/chapters/{chapter}")
    @SneakyThrows   
    @Operation(summary = "Get comic infomation", description = "Get comic infomation base on tag url with specific server id. Default server id is 0 - plugin's index in plugin list.")
    public ResponseSuccess<ComicChapterContent> getComicChapterContent(
                    @PathVariable(name = "tagId", required = true) String tagId,  
                    @PathVariable(name = "chapter", required = true) String currentChapter,
                    @RequestParam(name = "server_id", defaultValue = "0") int serverId
        ){ 
        var dataDto = pluginService.getComicChapterContent(serverId, tagId, currentChapter);
        return new ResponseSuccess<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }
    
    @GetMapping("/genres")
    @SneakyThrows   
    @Operation(summary = "Get all genres", description = "Get all genres with specific server id. Default server id is 0 - plugin's index in plugin list Default the current page,offset, is 1 Default limit, a number of comics per page is 10")
    public ResponseSuccess<List<Genre>> getGenres(
            @RequestParam(name = "server_id", defaultValue = "0") int serverId 
        ) {  
        var genres = pluginService.getAllGenres(serverId);  
        return new ResponseSuccess<>(HttpStatus.OK, "Success", genres);
    }
   
    @GetMapping("/lasted-comic")
    @SneakyThrows   
    @Operation(summary = "Get lasted comics", description = "Get lasted comics with specific server id. Default server id is 0 - plugin's index in plugin list Default current page is 1")
    public ResponseSuccess<List<ComicModel>> getNewestCommic(
        @RequestParam(name = "server_id", defaultValue = "0") int serverId,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) { 
        var dataDto = pluginService.getNewestCommic(serverId, page);   
        return new ResponseSuccess<>(HttpStatus.OK, "Success", dataDto.getPagination(), dataDto.getData());
    }

    @GetMapping("/plugins")
    @SneakyThrows
    @Operation(summary = "Get all plugins", description = "Get all plugins from the folder to crawl comic. Id of each plugin is the index of it in plugin list")
    public ResponseSuccess<List<Plugin>> getAllPlugins() {
        var plugins = pluginService.getAllPlugins(); 
        return new ResponseSuccess<>(HttpStatus.OK, "Success", plugins);
    }   
}
