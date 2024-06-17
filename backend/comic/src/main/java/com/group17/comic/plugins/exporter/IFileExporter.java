package com.group17.comic.plugins.exporter;

import java.util.UUID;

import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.tagging_interfaces.IPluginType;

public interface IFileExporter extends IPluginType {
    UUID getId();

    String getPluginName();

    String getBlobType();

    ChapterFile getConvertedFile(ChapterRequest chapterDto);
}
