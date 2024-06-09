package com.group17.comic;

import com.group17.comic.enums.PluginServiceType;
import com.group17.comic.factories.IPluginFactory;
import com.group17.comic.models.ConverterPlugin;
import com.group17.comic.models.CrawlerPlugin;
import com.group17.comic.service.ICrawlerPluginService;
import com.group17.comic.service.IExporterPluginService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class PluginTypeServiceTests {

    @Autowired
    private IPluginFactory pluginFactory;

    @Test
    public void checkAutoWiredDependencyTest(){
        assertThat(pluginFactory).isNotNull();
    }

    @Test
    public void getAllExporterPluginsTest(){
        var pluginService = (IExporterPluginService)pluginFactory.getPluginService(PluginServiceType.EXPORTER_SERVICE);
        List<ConverterPlugin> plugins = pluginService.getAllPlugins();
        assertThat(plugins).isNotNull();
        assertThat(plugins.size()).isGreaterThan(0);
        assertThat(plugins.size()).isEqualTo(5);
    }
    @Test
    public void  canGetAllCrawlerPlugins(){
        var pluginService = (ICrawlerPluginService)pluginFactory.getPluginService(PluginServiceType.CRAWLER_SERVICE);
        List<CrawlerPlugin> plugins = pluginService.getAllPlugins();
        assertThat(plugins).isNotNull();
        assertThat(plugins.size()).isGreaterThan(0);
        assertThat(plugins.size()).isEqualTo(3);
    }
}
