package com.group17.comic.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString; 

@Getter
@AllArgsConstructor
@ToString
@Builder
public class Comic {
    private Integer id;
    private String title;
    private String tag;
    private String image;
    private String alternateImage;
    private String description;
    private Author author; 
    private List<Genre> genres;
    private boolean isFull;
}
