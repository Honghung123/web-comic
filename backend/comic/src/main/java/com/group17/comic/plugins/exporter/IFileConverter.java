package com.group17.comic.plugins.exporter;

import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;

import java.util.UUID;

public interface IFileConverter {
    UUID getId();
    String getPluginName();
    String getBlobType();
    ChapterFile getConvertedFile(ChapterDTO chapterDto);
}
