package com.group17.comic.dto.request;

// Không thể dùng chapter bằng int vì server truyen chu th dung chuỗi string.
public record AlternatedChapterDTO(String title, String authorName, int chapterNo) {
} 