package com.group17.comic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthorResponseDTO {
    String authorId;
    String name;
    String comicTagId;
}
