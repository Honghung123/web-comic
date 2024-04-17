package com.group17.comic.model;

import java.util.List;

public record ComicModel(
    String tagId,
    String title,
    String image,
    String alternateImage,
    List<Genre> genres,
    int totalChapter,
    int newestChapter,
    String updatedTime
) { 
}
