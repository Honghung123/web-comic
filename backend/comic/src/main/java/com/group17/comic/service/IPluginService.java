package com.group17.comic.service;

import java.util.List;
import java.util.UUID;

public interface IPluginService<T> {
       UUID getDefaultPluginId();
       UUID getPluginIdByName(String name);
       Object getPluginById(UUID pluginId);
       void checkCurrentPlugins();
       List<T> getAllPlugins();
       void checkPluginList(List<String> pluginList);
}
