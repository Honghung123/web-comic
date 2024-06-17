package com.group17.comic.service;

import java.util.List;
import java.util.UUID;

import com.group17.comic.tagging_interfaces.IConcretePlugin;

public interface IPluginService<T extends IConcretePlugin> {
    UUID getDefaultPluginId();

    UUID getPluginIdByName(String name);

    Object getPluginById(UUID pluginId);

    void checkCurrentPlugins();

    List<T> getAllPlugins();

    void checkPluginList(List<String> pluginList);
}
