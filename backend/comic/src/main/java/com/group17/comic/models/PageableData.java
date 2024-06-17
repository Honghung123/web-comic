package com.group17.comic.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageableData<T, V> {
    private Pagination<T> pagination;
    private V data;
}
