package com.group17.comic.model; 
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ComicModel extends Comic{  
    private Integer totalChapter;
    private Integer newestChapter;
    private String updatedTime;  
}
