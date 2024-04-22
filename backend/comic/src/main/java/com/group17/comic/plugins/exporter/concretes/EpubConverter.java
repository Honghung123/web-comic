package com.group17.comic.plugins.exporter.concretes;

import java.io.File;
import java.io.FileInputStream; 
import java.nio.file.Files; 
import java.nio.file.Paths;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.plugins.exporter.IFileConverter;
import com.group17.comic.utils.FileUtility;
import com.group17.comic.utils.StringConverter;

import lombok.SneakyThrows;

public class EpubConverter implements IFileConverter {
    private static String uploadDir = "backend/comic/src/main/java/com/group17/comic/plugins/exporter/uploads/";

    @Override
    public String getPluginName() {
        return "EPUB";
    }

    @SneakyThrows
    @Override
    public ChapterFile getConvertedFile(ChapterDTO chapterDto) {
        String formatTitle = StringConverter.removeDiacriticalMarks(chapterDto.title());
        formatTitle = formatTitle.replaceAll("[^a-zA-Z0-9\\s]", "-").trim();
        String fileName = formatTitle + ".epub";
        // Wirte content to epub file and save it to folder 
        String uploadFolderAbsolutePath = Paths.get(uploadDir).toAbsolutePath().toString();
        File uploadFolderFile = new File(uploadFolderAbsolutePath);
        FileUtility.deleteDirectory(uploadFolderFile);
        FileUtility.createDirectory(uploadFolderFile);
        File destinationFile = Paths.get(uploadDir + fileName).toFile();
        // Get epub file from folder to return to client
        InputStreamResource resource = 
                    new InputStreamResource(new FileInputStream(uploadDir + fileName)); 
        FileUtility.deleteDirectory(uploadFolderFile); 
        HttpHeaders headers = new HttpHeaders();  
        headers.setContentLength(Files.size(Paths.get(uploadDir + fileName))); 
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);  
        return new ChapterFile(headers, resource);
    }    
}
