package com.group17.comic.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChapterDTO(
      @NotBlank(message = "Title cannot be blank")
      String title,

      @NotBlank(message = "Content cannot be blank")
      String content ) {
}
