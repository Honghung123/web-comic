package com.group17.comic.plugins.exporter;

import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;

public interface IFileConverter {
    String getPluginName();
    String getBlobType();
    ChapterFile getConvertedFile(ChapterDTO chapterDto);
}
