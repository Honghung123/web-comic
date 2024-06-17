package com.group17.comic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;

import com.group17.comic.utils.PluginUtility;

@SpringBootTest
class PluginUtilityTests {
    @TempDir
    Path tempDir;

    @Test
    void testResolveAbsolutePath_SuccessWithUnixPath() {
        String input = "/home/user/projects/backend/comic";
        String expected = "/home/user/projects/backend/comic";
        String actual = PluginUtility.resolveAbsolutePath(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveAbsolutePath_SuccessWithWindowsPath() {
        String input = "C:\\Users\\user\\projects\\backend\\comic";
        String expected = "C:\\Users\\user\\projects\\backend\\comic";
        String actual = PluginUtility.resolveAbsolutePath(input);
        assertEquals(expected, actual);
    }

    @Test
    void testGetAllFilesFromDirectory_Success() throws IOException {
        // Create temporary files
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.txt"));
        Files.createFile(tempDir.resolve("file3.txt"));
        List<File> files = PluginUtility.getAllFilesFromDirectory(tempDir.toString());
        assertEquals(3, files.size());
        List<String> fileNames = files.stream().map(File::getName).collect(Collectors.toList());
        assertEquals(List.of("file1.txt", "file2.txt", "file3.txt"), fileNames);
    }

    @Test
    void testGetAllFilesFromDirectory_EmptyDirectory() throws IOException {
        List<File> files = PluginUtility.getAllFilesFromDirectory(tempDir.toString());
        assertEquals(0, files.size());
    }

    @Test
    void testGetAllFilesFromDirectory_NonExistentDirectory() {
        String nonExistentPath = tempDir.resolve("nonExistentDir").toString();
        assertThrows(IOException.class, () -> PluginUtility.getAllFilesFromDirectory(nonExistentPath));
    }
}
