package com.group17.comic.models;

import lombok.AllArgsConstructor; 
import lombok.Getter;

import java.util.UUID;

@Getter 
@AllArgsConstructor  
public class CrawlerPlugin {
    UUID id;
    String name;
}
