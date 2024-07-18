package com.group17.comic.utils;

import com.group17.comic.plugins.crawler.IDataCrawler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginUtility {
    private PluginUtility() {}

    public static <T> List<T> getAllPluginsFromFolder(
            String concretePath, String pluginPackageName, Class<?> targetInterface)
            throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
                    InstantiationException, IllegalAccessException {
        if (!targetInterface.isInterface()) {
            throw new IllegalAccessException("The class is not an interface");
        }
        var pluginClasses =
                getAllPluginsFromFolderWithoutInstantiation(concretePath, pluginPackageName, targetInterface);
        List<T> plugins = new ArrayList<>();
        System.out.println("Start instantiation");
        for (var clazz : pluginClasses) {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            T plugin = (T) constructor.newInstance();
            plugins.add(plugin);
        }
        return plugins;
    }

    public static List<Class<?>> getAllPluginsFromFolderWithoutInstantiation(
            String concretePath, String pluginPackageName, Class<?> targetInterface)
            throws IOException, ClassNotFoundException, IllegalAccessException {
        if (!targetInterface.isInterface()) {
            throw new IllegalAccessException("The class is not an interface");
        }
        List<File> files = getAllFilesFromDirectory(concretePath);
        System.out.println("Get all files from directory successfully: " + files.size());
        List<Class<?>> pluginClasses = new ArrayList<>();
        for (File file : files) {
            var clazz = getClassInstance(file, pluginPackageName);
            if (clazz != null) {
                boolean isImplemented = targetInterface.isAssignableFrom(clazz);
                if (isImplemented) {
                    pluginClasses.add(clazz);
                }
            }
        }
        return pluginClasses;
    }

    public static Class<?> getClassInstance(File filePath, String packageName)
            throws IOException, ClassNotFoundException {
        String fileName = filePath.getName().split("\\.")[0];
        String extension = filePath.getName().split("\\.")[1];
        if (extension.equals("java")) {
            URL url = filePath.toURI().toURL();
            URL newUrl = new URL(url.toString().replaceAll("app/", "app/backend/comic/"));
            System.out.println("Url: " + newUrl);
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {newUrl});
            Class<?> clazz = classLoader.loadClass(packageName + "." + fileName);
            classLoader.close();
            return clazz;
        }
        return null;
    }

    public static List<File> getAllFilesFromDirectory(String absolutePath) throws IOException {
        System.out.println(absolutePath);
        String resolvedPath = absolutePath.replaceAll("/app/backend/comic/", "");
        Path pluginDirectory = Paths.get(resolvedPath);
        return Files.list(pluginDirectory).map(Path::toFile).toList();
    }

    public static String resolveAbsolutePath(String absolutePath) {
        String[] basePath = {"/backend/comic", "\\backend\\comic"};
        boolean isEndsWithBasePath = Arrays.stream(basePath).anyMatch(absolutePath::endsWith);
        if (isEndsWithBasePath) {
            return absolutePath;
        }
        return absolutePath + basePath[0];
    }
}
