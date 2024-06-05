package com.group17.comic.factories;

import com.group17.comic.enums.PluginServiceType;
import com.group17.comic.service.IPluginService;

public interface IPluginFactory {
    IPluginService getPluginService(PluginServiceType plugin);
}
