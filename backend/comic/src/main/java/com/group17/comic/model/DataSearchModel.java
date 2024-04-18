package com.group17.comic.model;

import lombok.Getter;

@Getter
public class DataSearchModel<T, V, U> extends DataModel<T, V> {
    private U meta;
    public DataSearchModel(Pagination<T> pagination, V data, U meta) {
        super(pagination, data);
        this.meta = meta;
    }
}
