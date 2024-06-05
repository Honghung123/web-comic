package com.group17.comic.plugins.exporter;

import com.group17.comic.dto.request.ChapterRequest;
import com.group17.comic.dto.response.ChapterFile;

import java.util.UUID;

public interface IFileExporter {
    UUID getId();
    String getPluginName();
    String getBlobType();
    ChapterFile getConvertedFile(ChapterRequest chapterDto);

}
