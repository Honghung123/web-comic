package com.group17.comic.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthorResponse {
    String authorId;
    String name;
    String comicTagId;
}
