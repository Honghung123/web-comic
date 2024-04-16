package com.group17.comic.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder; 
import lombok.Getter; 

@Getter
@Builder
@AllArgsConstructor 
public class DataModel<T extends List<?>> {
    private Pagination pagination;
    private T data;
}
