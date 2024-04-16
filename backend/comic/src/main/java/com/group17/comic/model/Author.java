package com.group17.comic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter; 
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@ToString
public class Author {
    Integer authorId;
    String name;
}
