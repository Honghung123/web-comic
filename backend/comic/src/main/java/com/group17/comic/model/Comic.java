package com.group17.comic.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter; 

@Getter 
@AllArgsConstructor
@Builder
public class Comic {
    private String tagId;
    private String title; 
    private String image;
    private String alternateImage;
    private String description;
    private Author author; 
    private List<Genre> genres;
    private boolean isFull;
}
