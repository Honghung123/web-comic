package com.group17.comic.plugins.exporter;

import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.tagging_interfaces.IPluginType;

import java.util.UUID;

public interface IFileExporter extends IPluginType {
    UUID getId();
    String getPluginName();
    String getBlobType();
    ChapterFile getConvertedFile(ChapterRequest chapterDto);
}
