package com.group17.comic.model;
  
import lombok.Getter;  

@Getter 
public class Chapter {
    private Integer chapterNo;
    private String title;
    public Chapter(int chapterNo, String title){
        this.chapterNo = chapterNo;
        this.title = title; 
    }
}
