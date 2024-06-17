package com.group17.comic.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionType {
    COMIC_NOT_FOUND(404, "Comic not found"),
    INVALID_GENRE(400, "Invalid genre"),
    GET_COMIC_INFO_FAILED(500, "Get comic info failed"),
    GET_COMIC_TAGID_FAILED(404, "Get comic tag id failed"),
    GET_COMIC_CHAPTER_CONTENT_FAILED(404, "Get comic chapter content failed"),
    GET_COMIC_CHAPTER_LIST_FAILED(404, "Get comic chapter list failed"),
    GET_LASTED_COMIC_FAILED(404, "Get lasted comic failed"),
    GET_AUTHOR_LIST_FAILED(404, "Get author list failed"),
    GET_GENRE_LIST_FAILED(404, "Get genre list failed"),
    GET_COMIC_LIST_FAILED(404, "Get comic list failed"),
    GET_CHAPTER_TITLE_FAILED(404, "Get chapter title failed"),
    REQUEST_SERVER_TO_CRAWL_FAILED(500, "Request server to crawl failed"),
    INVALID_COMIC_TAG_ID(400, "Invalid comic tag id"),
    PLUGIN_NOT_FOUND(500, "Plugin not found"),
    PLUGIN_LIST_CHANGED(400, "Plugin list changed. Please refresh page"),
    INVALID_PLUGIN_SERVICE_TYPE(500, "Invalid plugin service type"),
    TRACK_CONVERT_PROGRESS_FAILED(500, "Track convert progress failed"),
    GET_CONVERTED_FILE_FAILED(500, "Get converted file failed"),
    PLUGIN_SERVICE_NOT_FOUND(500, "Plugin service not found"),
    INVALID_PLUGIN_CONCRETE_TYPE(500, "Invalid plugin concrete type"),
    INVALID_PLUGIN_ID_LIST(400, "Invalid plugin id list");

    private final int code;
    private final String message;
}
