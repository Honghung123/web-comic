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
import java.util.List;
import java.util.stream.Collectors;

public class PluginUtility {
    /**
     * Retrieves all plugins from a plugin folder.
     *
     * @return          a list of WebCrawler plugins
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getAllPluginsFromFolder(String concreteRelativePath, String pluginPackageName,
            Class<?> intefaces) throws IOException, ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!intefaces.isInterface()) {
            throw new IllegalAccessException("The class is not an interface");
        }
        List<File> files = getAllFilesFromDirectory(concreteRelativePath);
        List<T> plugins = new ArrayList<>();
        for (File file : files) {
            var clazz = getClassInstance(file, pluginPackageName);
            if (clazz != null) {
                boolean isImplemented = intefaces.isAssignableFrom(clazz); // not compatible for type T 
                if (isImplemented) {
                    Constructor<?> constructor = clazz.getDeclaredConstructor();
                    T plugin = (T) constructor.newInstance();
                    plugins.add(plugin);
                }
            }
        }
        return plugins;
    }

    /**
     * A function to get class instance of type Class from file path
     *
     * @param  filePath         the directory of the file
     * @param  packageName      the package name of the file
     * @return                 return the instance of the class which has type Class
     */
    public static Class<?> getClassInstance(File filePath, String packageName)
            throws IOException, ClassNotFoundException {
        String fileName = filePath.getName().split("\\.")[0];
        String extension = filePath.getName().split("\\.")[1];
        if (extension.equals("java")) {
            URL url = filePath.toURI().toURL();
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { url });
            Class<?> clazz = classLoader.loadClass(packageName + "." + fileName);
            classLoader.close();
            return clazz;
        }
        return null;
    }

    /**
     * Retrieves all files in the specified directory.
     *
     * @param  relativePath   the relative directory path
     * @return               a list of files having type of File in the directory
     * @throws IOException   if an I/O error occurs
     */
    public static List<File> getAllFilesFromDirectory(String relativePath) throws IOException {
        Path pluginDirectory = Paths.get(relativePath).toAbsolutePath(); 
        return Files.list(pluginDirectory)
                .map(Path::toFile)
                .collect(Collectors.toList());
    }
}