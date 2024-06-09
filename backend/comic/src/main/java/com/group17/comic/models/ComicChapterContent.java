package com.group17.comic.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComicChapterContent {
    private String title;
    private String chapterTitle;
    private String content;
    private String comicTagId;
    private Author author;
    private int chapterNumber;
}
