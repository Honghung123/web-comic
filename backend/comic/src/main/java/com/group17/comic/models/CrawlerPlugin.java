package com.group17.comic.models;

import com.group17.comic.tagging_interfaces.IConcretePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter 
@AllArgsConstructor  
public class CrawlerPlugin implements IConcretePlugin {
    UUID id;
    String name;
}
