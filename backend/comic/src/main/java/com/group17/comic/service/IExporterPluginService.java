package com.group17.comic.service;

import com.group17.comic.dto.request.ChapterRequest;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.model.ConverterPlugin;

import java.util.UUID;

public interface IExporterPluginService extends IPluginService<ConverterPlugin>{
    ChapterFile exportFile(ChapterRequest chapterDto, UUID converterId);
}
