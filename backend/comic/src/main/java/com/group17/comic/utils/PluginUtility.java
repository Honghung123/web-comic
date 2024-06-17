package com.group17.comic.utils;

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
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {url});
            Class<?> clazz = classLoader.loadClass(packageName + "." + fileName);
            classLoader.close();
            return clazz;
        }
        return null;
    }

    public static List<File> getAllFilesFromDirectory(String absolutePath) throws IOException {
        Path pluginDirectory = Paths.get(absolutePath);
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
