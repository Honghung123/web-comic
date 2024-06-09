package com.group17.comic;

import com.group17.comic.utils.PluginUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PluginUtilityTests {
    @TempDir
    Path tempDir;

    @Test
    public void testResolveAbsolutePath_SuccessWithUnixPath() {
        String input = "/home/user/projects/backend/comic";
        String expected = "/home/user/projects/backend/comic";
        String actual = PluginUtility.resolveAbsolutePath(input);
        assertEquals(expected, actual);
    }

    @Test
    public void testResolveAbsolutePath_SuccessWithWindowsPath() {
        String input = "C:\\Users\\user\\projects\\backend\\comic";
        String expected = "C:\\Users\\user\\projects\\backend\\comic";
        String actual = PluginUtility.resolveAbsolutePath(input);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllFilesFromDirectory_Success() throws IOException {
        // Create temporary files
        Path file1 = Files.createFile(tempDir.resolve("file1.txt"));
        Path file2 = Files.createFile(tempDir.resolve("file2.txt"));
        Path file3 = Files.createFile(tempDir.resolve("file3.txt"));
        List<File> files = PluginUtility.getAllFilesFromDirectory(tempDir.toString());
        assertEquals(3, files.size());
        List<String> fileNames = files.stream().map(File::getName).collect(Collectors.toList());
        assertEquals(List.of("file1.txt", "file2.txt", "file3.txt"), fileNames);
    }

    @Test
    public void testGetAllFilesFromDirectory_EmptyDirectory() throws IOException {
        List<File> files = PluginUtility.getAllFilesFromDirectory(tempDir.toString());
        assertEquals(0, files.size());
    }

    @Test
    public void testGetAllFilesFromDirectory_NonExistentDirectory() {
        String nonExistentPath = tempDir.resolve("nonExistentDir").toString();
        assertThrows(IOException.class, () -> PluginUtility.getAllFilesFromDirectory(nonExistentPath));
    }

}
