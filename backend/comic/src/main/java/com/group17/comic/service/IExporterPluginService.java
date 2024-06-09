package com.group17.comic.service;

import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.models.ConverterPlugin;

import java.util.UUID;

public interface IExporterPluginService extends IPluginService<ConverterPlugin>{
    ChapterFile exportFile(ChapterRequest chapterDto, UUID converterId);
}
