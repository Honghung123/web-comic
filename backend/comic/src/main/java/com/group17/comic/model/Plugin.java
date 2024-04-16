package com.group17.comic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter; 

@Getter 
@AllArgsConstructor 
@Builder
public class Plugin {
    Integer id;
    String name; 
}
