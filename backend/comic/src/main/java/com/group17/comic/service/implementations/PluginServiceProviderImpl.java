package com.group17.comic.service.implementations;

import com.group17.comic.enums.PluginServiceType;
import com.group17.comic.service.ICrawlerPluginService;
import com.group17.comic.service.IPluginService;
import com.group17.comic.factories.IPluginFactory;
import com.group17.comic.service.IPluginServiceProvider;
import com.group17.comic.utils.StringUtility;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("pluginServiceProviderV1")
@RequiredArgsConstructor
public class PluginServiceProviderImpl implements IPluginServiceProvider {
    private final IPluginFactory pluginFactory;
    @SneakyThrows
    @Override
    public UUID getDefaultPluginId(IPluginService pluginService){
        return pluginService.getDefaultPluginId();
    }

    @Override
    public void examinePluginList(String pluginList, PluginServiceType serviceType){
        List<String> crawlerList = StringUtility.getArrayFromJSON(pluginList);
        var crawlerService = this.getPluginServiceByType(serviceType);
        crawlerService.checkPluginList(crawlerList);
    }

    @Override
    public IPluginService getPluginServiceByType(PluginServiceType pluginType){
        return pluginFactory.getPluginService(pluginType);
    }
}
