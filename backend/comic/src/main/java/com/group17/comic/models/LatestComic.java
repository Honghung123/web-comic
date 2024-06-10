package com.group17.comic.models;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LatestComic extends Comic {
    private Integer totalChapter;
    private Integer newestChapter;
    private String updatedTime;
}
