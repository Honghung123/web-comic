package com.group17.comic.service ;
 
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Service;

import com.group17.comic.log.Logger;
import com.group17.comic.model.*; 
import com.group17.comic.plugins.IDataCrawler;
import com.group17.comic.utils.PluginUtility; 

import java.io.IOException; 
import java.lang.reflect.InvocationTargetException; 
import java.util.ArrayList; 
import java.util.List; 

@Service 
public class PluginService {
    @Value("${comic.base_dir}") String projectDirectory;
    @Value("${comic.plugin.plugin_package_name}") String pluginPackageName;
    @Value("${comic.plugin.plugin_directory}") String pluginDirectory;
    private List<IDataCrawler> plugins = new ArrayList<>();
 
    private void checkPlugins() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        if(plugins.isEmpty()) {
            Logger.logInfo("There are currently no plugins. Loading plugins from plugin directory");
            plugins = PluginUtility.getAllPluginsFromFolder(projectDirectory + pluginDirectory, pluginPackageName);
        }
    }
    
    /**
     * The function get all plugins available.
     *
     * @return         return a list of all plugins
     */
    public List<Plugin> getAllPlugins() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        checkPlugins();
        List<Plugin> pluginList = new ArrayList<>();
        int index = 0;
        for (var plugin : plugins) {
            String pluginClassName = plugin.getClass().getSimpleName();
            String pluginName = pluginClassName.substring(0, pluginClassName.lastIndexOf("Crawler"));
            pluginList.add(new Plugin(index, pluginName));
            index++;
        }
        return pluginList;
    } 
 
    
    public List<Genre> getAllGenres(int pluginId) throws Exception {
        checkPlugins();
        var result = plugins.get(pluginId).getGenres();           
        return result;
    } 
    
    public DataModel<Integer, List<ComicModel>> getNewestCommic(int pluginId, int page) throws Exception {
        checkPlugins();
        var result = plugins.get(pluginId).getLastedComics(page);   
        
        return result;
    }


    public Comic getComicInfo(int pluginId, String tagUrl) throws Exception {
        checkPlugins();
        var comic = plugins.get(pluginId).getComicInfo(tagUrl);   
        return comic;
    }


    public DataSearchModel<Integer, List<ComicModel>, List<Author>> searchComic(int serverId, String keyword, int currentPage) throws Exception {
        checkPlugins();
        var result = plugins.get(serverId).search(keyword, currentPage);
        return result;
    }

    public DataModel<Integer, List<Chapter>> getChapters(int serverId, String tagId, int currentPage) throws Exception {
        checkPlugins();
        var result = plugins.get(serverId).getChapters(tagId, currentPage);
        return result;
    }

    public DataModel<?, ComicChapterContent> getComicChapterContent(int serverId, String tagId, String currentChapter) throws Exception{
        checkPlugins();
        var result = plugins.get(serverId).getComicChapterContent(tagId, currentChapter);
        return result; 
    } 
}
