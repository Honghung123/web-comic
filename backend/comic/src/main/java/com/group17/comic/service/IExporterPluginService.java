package com.group17.comic.service;

import java.util.UUID;

import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.models.ConverterPlugin;

public interface IExporterPluginService extends IPluginService<ConverterPlugin> {
    ChapterFile exportFile(ChapterRequest chapterDto, UUID converterId);
}
