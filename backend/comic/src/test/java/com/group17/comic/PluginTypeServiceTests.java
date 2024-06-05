package com.group17.comic;

import com.group17.comic.model.ConverterPlugin;
import com.group17.comic.model.CrawlerPlugin;
import com.group17.comic.service.ICrawlerPluginService;
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
class PluginTypeServiceTests {

    @Autowired
    @Qualifier("crawlerPluginServiceV1")
    private ICrawlerPluginService pluginService;

    @Test
    public void checkAutoWiredDependencyTest(){
        assertThat(pluginService).isNotNull();
        assertThat(pluginService).isInstanceOf(IPluginService.class);
    }

    @Test
    public void getAllConverterPluginsTest(){
//        List<ConverterPlugin> plugins = pluginService.getAllPlugins();
//        assertThat(plugins).isNotNull();
//        assertThat(plugins.size()).isGreaterThan(0);
    }
    @Test
    public void  canGetAllCrawlerPlugins(){
        List<CrawlerPlugin> plugins = pluginService.getAllPlugins();
        assertThat(plugins).isNotNull();
        assertThat(plugins.size()).isGreaterThan(0);
    }
    @Test
    public void canCheckCrawlerPlugins(){
        pluginService.checkCurrentPlugins();

    }
    @Test
    public void getCrawlerPluginTest(){
        String pluginName = "Tang Thu Vien";

    }



}
