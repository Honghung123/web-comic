package com.group17.comic.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
class Navigation<T> {
    private T prevPage;
    private T nextPage;
}

@Getter
@ToString
public class Pagination<T> {
    private T currentPage;
    private Integer perPage;
    private Integer totalItems;
    private Integer totalPages;
    private Navigation<T> link;

    public Pagination(T currentPage, Integer perPage, Integer totalPages, Integer totalItems) {
        this.currentPage = currentPage;
        this.perPage = perPage;
        this.totalPages = totalPages;
        this.link = new Navigation<>();
        this.totalItems = totalItems;
    }

    public void setNextPage(T page) {
        this.link.setNextPage(page);
    }

    public void setPreviousPage(T page) {
        this.link.setPrevPage(page);
    }
}
