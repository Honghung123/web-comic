package com.group17.comic.dtos.response;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChapterFile {
    HttpHeaders headers;
    InputStreamResource resource;
}
