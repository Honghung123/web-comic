package com.group17.comic.service;

import com.group17.comic.enums.PluginServiceType;

import java.util.UUID;

public interface IPluginServiceProvider {
    UUID getDefaultPluginId(IPluginService pluginService);
    void examinePluginList(String pluginList, PluginServiceType serviceType);
    IPluginService getPluginServiceByType(PluginServiceType serviceType);
}
