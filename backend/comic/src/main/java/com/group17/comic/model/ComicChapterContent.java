package com.group17.comic.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComicChapterContent {
    private String title;
    private String content;
    private String comicTagId;
    private Author author;
}
