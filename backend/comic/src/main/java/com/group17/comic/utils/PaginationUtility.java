package com.group17.comic.utils;

import com.group17.comic.models.Pagination;

public class PaginationUtility {
    private PaginationUtility() {}

    public static void updatePagination(Pagination<Integer> pagination) {
        Integer currentPage = pagination.getCurrentPage();
        Integer totalPages = pagination.getTotalPages();
        Integer nextPage = currentPage < totalPages ? currentPage + 1 : null;
        Integer prevPage = currentPage > 1 ? currentPage - 1 : null;
        pagination.setNextPage(nextPage);
        pagination.setPreviousPage(prevPage);
    }
}
