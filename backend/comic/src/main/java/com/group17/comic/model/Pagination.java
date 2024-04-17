package com.group17.comic.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
class Navigation{
    private Integer prevPage;
    private Integer nextPage;
}
 
@Getter  
@ToString
public class Pagination{
    private Integer currentPage;
    private Integer perPage;
    private Integer totalItems;
    private Integer totalPages;
    private Navigation link;
    public Pagination(Integer currentPage, Integer perPage, Integer totalPages, Integer totalItems) {
        this.currentPage = currentPage;
        this.perPage = perPage;
        this.totalPages = totalPages;
        this.link = new Navigation();
        this.link.setPrevPage(currentPage - 1 > 0 ? currentPage - 1 : 1);
        this.link.setNextPage(currentPage + 1 <= totalPages ? currentPage + 1 : totalPages);
        this.totalItems = totalItems;
    }
    public void setNextPage(int page) {
        this.link.setNextPage(page);
    }

    public void setPreviousPage(int page) {
        this.link.setPrevPage(page);
    }
}