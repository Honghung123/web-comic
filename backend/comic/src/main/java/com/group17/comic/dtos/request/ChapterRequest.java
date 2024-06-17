package com.group17.comic.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record ChapterRequest(
        @NotBlank(message = "Title cannot be blank") String title,
        @NotBlank(message = "Content cannot be blank") String content) {}
