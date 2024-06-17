package com.group17.comic.plugins.exporter.concretes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.plugins.exporter.IFileExporter;
import com.group17.comic.utils.FileUtility;
import com.group17.comic.utils.StringUtility;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DocxExporter implements IFileExporter {
    private static final String UPLOAD_DIR = "backend/comic/src/main/java/com/group17/comic/plugins/exporter/uploads/";
    private static final UUID PLUGIN_ID = UUID.randomUUID();

    @Override
    public UUID getId() {
        return PLUGIN_ID;
    }

    @Override
    public String getPluginName() {
        return "DOCX";
    }

    @Override
    public String getBlobType() {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    }

    @SneakyThrows
    @Override
    public ChapterFile getConvertedFile(ChapterRequest chapterDto) {
        String formatedTitle = StringUtility.removeDiacriticalMarks(chapterDto.title())
                .replaceAll("[^a-zA-Z0-9]", "-")
                .trim();
        String htmlFile = formatedTitle + ".html";
        String fileOutputName = formatedTitle + ".docx";
        // Create a html file and save it to folder
        String simpleHtmlContent = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<title>Sample HTML File</title>\n"
                + "</head>\n"
                + "<body>\n"
                + chapterDto.content()
                + "\n"
                + "</body>\n"
                + "</html>";
        String htmlFilePath = UPLOAD_DIR + htmlFile;
        String uploadFolderAbsolutePath = Paths.get(UPLOAD_DIR).toAbsolutePath().toString();
        File uploadFolderFile = new File(uploadFolderAbsolutePath);
        FileUtility.deleteDirectory(uploadFolderFile);
        FileUtility.createDirectory(uploadFolderFile);
        FileUtility.createFile(htmlFilePath, simpleHtmlContent);
        // Then get the saved html file from folder to convert to docx, and download it afterwards
        byte[] fileBytes = this.convertFileOnline(htmlFile, htmlFilePath, fileOutputName);
        // Next, save download converted docx to folder
        File destinationFile = Paths.get(UPLOAD_DIR + fileOutputName).toFile();
        FileUtility.saveDownloadedBytesToFolder(fileBytes, destinationFile);
        // Finally, get the saved docx file from folder to return to client
        InputStreamResource resource = new InputStreamResource(new FileInputStream(UPLOAD_DIR + fileOutputName));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(Files.size(Paths.get(UPLOAD_DIR + fileOutputName)));
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        return new ChapterFile(headers, resource);
    }

    @SuppressWarnings("deprecation")
    @SneakyThrows
    private byte[] convertFileOnline(String htmlFile, String htmlFilePath, String fileOutputName) {
        String api = "https://api.apyhub.com/convert/html-file/doc-file?output=";
        String apyToken = "APY0Xu6XpJitjtR6YduOHqBpPgg3d6XbDm5SNb4R6ava3bX81L2jjuEtLiJ4jtG1xjqz4GNqy";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file", htmlFile, RequestBody.create(MediaType.parse("text/html"), new File(htmlFilePath)))
                .build();
        Request request = new Request.Builder()
                .url(api + fileOutputName)
                .post(requestBody)
                .header("apy-token", apyToken)
                .header("content-type", "multipart/form-data")
                .build();
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().bytes();
        }
    }
}
