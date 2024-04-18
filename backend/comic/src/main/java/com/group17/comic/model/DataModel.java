package com.group17.comic.model;
 

import lombok.AllArgsConstructor; 
import lombok.Getter; 

@Getter 
@AllArgsConstructor 
public class DataModel<T, V> {
    private Pagination<T> pagination;
    private V data;
}
