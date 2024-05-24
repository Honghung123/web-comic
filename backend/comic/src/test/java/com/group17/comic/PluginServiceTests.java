package com.group17.comic;

import com.group17.comic.model.ConverterPlugin;
import com.group17.comic.model.CrawlerPlugin;
import com.group17.comic.service.IPluginService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class PluginServiceTests {

    @Autowired
    @Qualifier("pluginServiceV1")
    private IPluginService pluginService;

    @Test
    public void checkAutoWiredDependencyTest(){
        assertThat(pluginService).isNotNull();
        assertThat(pluginService).isInstanceOf(IPluginService.class);
    }

    @Test
    public void getAllConverterPluginsTest(){
        List<ConverterPlugin> plugins = pluginService.getAllConverterPlugins();
        assertThat(plugins).isNotNull();
        assertThat(plugins.size()).isGreaterThan(0);
    }
    @Test
    public void  canGetAllCrawlerPlugins(){
        List<CrawlerPlugin> plugins = pluginService.getAllCrawlerPlugins();
        assertThat(plugins).isNotNull();
        assertThat(plugins.size()).isGreaterThan(0);
    }
    @Test
    public void canCheckCrawlerPlugins(){
        pluginService.checkCrawlerPlugins();

    }
    @Test
    public void getCrawlerPluginTest(){
        String pluginName = "Tang Thu Vien";

    }



}
