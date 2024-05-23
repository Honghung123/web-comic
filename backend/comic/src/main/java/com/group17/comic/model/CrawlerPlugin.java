package com.group17.comic.model;

import lombok.AllArgsConstructor; 
import lombok.Getter;

import java.util.UUID;

@Getter 
@AllArgsConstructor  
public class CrawlerPlugin {
    UUID id;
    String name;

}
