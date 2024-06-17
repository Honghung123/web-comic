package com.group17.comic.models;

import lombok.Getter;

@Getter
public class SearchingPageableData<T, V, U> extends PageableData<T, V> {
    private U meta;

    public SearchingPageableData(Pagination<T> pagination, V data, U meta) {
        super(pagination, data);
        this.meta = meta;
    }
}
