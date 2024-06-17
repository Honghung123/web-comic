package com.group17.comic.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@SuperBuilder
public class Comic {
    private String tagId;
    private String title;
    private String image;
    private String alternateImage;
    private String description;
    private Author author;
    private List<Genre> genres;
    private Double rate;
    private boolean isFull;
}
