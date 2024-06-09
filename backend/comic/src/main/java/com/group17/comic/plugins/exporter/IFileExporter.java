package com.group17.comic.plugins.exporter;

import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;

import java.util.UUID;

public interface IFileExporter {
    UUID getId();
    String getPluginName();
    String getBlobType();
    ChapterFile getConvertedFile(ChapterRequest chapterDto);
}
