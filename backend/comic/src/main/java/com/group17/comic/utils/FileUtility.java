package com.group17.comic.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import lombok.SneakyThrows;

public class FileUtility {
    private FileUtility() {}

    public static boolean createDirectory(File file) {
        if (!file.exists()) {
            return file.mkdir();
        }
        return false;
    }

    public static boolean deleteDirectory(File folder) {
        if (!folder.exists()) {
            return false;
        }
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        return folder.delete();
    }

    @SneakyThrows
    public static boolean createFile(String filePath, String content) {
        if (content == null) {
            content = "";
        }
        // create new file and write content into it
        try (FileOutputStream fos = new FileOutputStream(filePath);
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                BufferedWriter writer = new BufferedWriter(osw)) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            throw new IOException("Failed to write to file: ", e);
        }
    }

    public static void saveDownloadedBytesToFolder(byte[] fileBytes, File destinationFile) throws IOException {
        OutputStream output = new FileOutputStream(destinationFile);
        output.write(fileBytes);
        output.flush();
        output.close();
    }
}
