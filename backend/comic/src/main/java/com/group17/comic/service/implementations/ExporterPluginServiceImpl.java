package com.group17.comic.service.implementations;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.enums.ExceptionType;
import com.group17.comic.exceptions.BusinessException;
import com.group17.comic.models.ConverterPlugin;
import com.group17.comic.plugins.exporter.IFileExporter;
import com.group17.comic.service.IExporterPluginService;
import com.group17.comic.utils.ListUtility;
import com.group17.comic.utils.PluginUtility;

import lombok.SneakyThrows;

@Service("exporterPluginServiceV1")
public class ExporterPluginServiceImpl implements IExporterPluginService {
    String baseDir = System.getProperty("user.dir");

    @Value("${comic.base_dir}")
    String projectDirectory;

    @Value("${comic.plugin.converter.converter_package_name}")
    String exporterPackageName;

    @Value("${comic.plugin.converter.converter_directory}")
    String exporterDirectory;

    private List<IFileExporter> exporters = new ArrayList<>();

    @Value("${comic.plugin.converter.default_converter_name}")
    private String DEFAULT_CONVERTER;

    @Override
    public Object getPluginById(UUID pluginId) {
        return exporters.stream()
                .filter(exporter -> exporter.getId().equals(pluginId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionType.PLUGIN_NOT_FOUND));
    }

    @SneakyThrows
    @Override
    public void checkCurrentPlugins() {
        baseDir = PluginUtility.resolveAbsolutePath(System.getProperty("user.dir"));
        Path converterAbsolutePath = Paths.get(baseDir, projectDirectory, exporterDirectory);
        var exporterClasses = PluginUtility.getAllPluginsFromFolderWithoutInstantiation(
                converterAbsolutePath.toString(), exporterPackageName, IFileExporter.class);
        if (exporters.isEmpty() || exporterClasses.size() != exporters.size()) {
            exporters = PluginUtility.getAllPluginsFromFolder(
                    converterAbsolutePath.toString(), exporterPackageName, IFileExporter.class);
        }
    }

    @SneakyThrows
    @Override
    public List<ConverterPlugin> getAllPlugins() {
        this.checkCurrentPlugins();
        List<ConverterPlugin> pluginList = new ArrayList<>();
        for (var exporter : exporters) {
            String pluginName = exporter.getPluginName();
            String blobType = exporter.getBlobType();
            UUID id = exporter.getId();
            pluginList.add(new ConverterPlugin(id, pluginName, blobType));
        }
        return pluginList;
    }

    @SneakyThrows
    @Override
    public ChapterFile exportFile(ChapterRequest chapterDto, UUID converterId) {
        var exporterPlugin = exporters.stream()
                .filter(exporter -> exporter.getId().equals(converterId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionType.PLUGIN_NOT_FOUND));
        return exporterPlugin.getConvertedFile(chapterDto);
    }

    @Override
    public UUID getDefaultPluginId() {
        return this.getPluginIdByName(DEFAULT_CONVERTER);
    }

    @Override
    public UUID getPluginIdByName(String name) {
        var exporterPlugin = exporters.stream()
                .filter(exporter -> exporter.getPluginName().equals(name))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionType.PLUGIN_NOT_FOUND));
        return exporterPlugin.getId();
    }

    @SneakyThrows
    @Override
    public void checkPluginList(List<String> pluginList) {
        this.checkCurrentPlugins();
        List<String> exporterIdList =
                exporters.stream().map(exporter -> exporter.getId().toString()).toList();
        if (!pluginList.isEmpty() && !ListUtility.areListsEqual(pluginList, exporterIdList)) {
            throw new BusinessException(ExceptionType.PLUGIN_LIST_CHANGED);
        }
    }
}
