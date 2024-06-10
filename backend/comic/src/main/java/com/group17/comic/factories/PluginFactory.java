package com.group17.comic.factories;

import com.group17.comic.enums.PluginServiceType;
import com.group17.comic.service.ICrawlerPluginService;
import com.group17.comic.service.IExporterPluginService;
import com.group17.comic.service.IPluginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PluginFactory implements IPluginFactory{
    @Qualifier("crawlerPluginServiceV1")
    @Lazy
    private final ICrawlerPluginService crawlerPluginService;
    @Qualifier("exporterPluginServiceV1")
    private final IExporterPluginService exporterPluginService;

    @Override
    @Lazy
    public IPluginService getPluginService(PluginServiceType plugin){
        switch (plugin){
            case CRAWLER_SERVICE:
                return crawlerPluginService;
            case EXPORTER_SERVICE:
                return exporterPluginService;
            default:
                return null;
        }
    }
}
