package com.group17.comic.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Không thể dùng chapter bằng int vì server truyen chu th dung chuỗi string.
public record AlternatedChapterRequest(
        @NotBlank(message = "Title cannot be blank") String title,
        @NotBlank(message = "Author name cannot be blank") String authorName,
        @NotBlank(message = "Tag id cannot be blank") String comicTagId,
        @Min(value = 1, message = "Chapter number must be greater than 1") int chapterNumber) {}
