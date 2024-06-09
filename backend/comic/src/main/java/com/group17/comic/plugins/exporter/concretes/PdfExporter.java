package com.group17.comic.plugins.exporter.concretes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.group17.comic.utils.FileUtility;
import com.group17.comic.utils.StringUtility;
import lombok.SneakyThrows;
import okhttp3.*;

import org.jsoup.HttpStatusException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders; 

import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.plugins.exporter.IFileExporter;

/**
 * @author: estakov
 * @date: 20.04.2024
 * @source: https://github.com/bytescout/pdf-co-api-samples/tree/master/PDF%20from%20HTML/Java/Generate%20PDF%20From%20HTML%20File/src/com/company
 * 
 */

public class PdfExporter implements IFileExporter {
    private static String uploadDir = "backend/comic/src/main/java/com/group17/comic/plugins/exporter/uploads/";
    private final UUID id = UUID.randomUUID();
    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getPluginName() {
        return "PDF";
    }

    @Override
    public String getBlobType() {
        return "application/pdf";
    }

    @Override
    @SneakyThrows
    public ChapterFile getConvertedFile(ChapterRequest chapterDto) {
        String formatTitile = StringUtility.removeDiacriticalMarks(chapterDto.title());
        formatTitile = formatTitile.replaceAll("[^a-zA-Z0-9]", "-").trim();
        String fileName = formatTitile + ".pdf";
        // Convert html to pdf online, and download it afterwards 
        byte[] fileBytes = this.savePdfFromText(chapterDto.content(), fileName);
        // Then save the pdf file to folder  
        String uploadFolderAbsolutePath = Paths.get(uploadDir).toAbsolutePath().toString();
        File uploadFolderFile = new File(uploadFolderAbsolutePath);
        FileUtility.deleteDirectory(uploadFolderFile);
        FileUtility.createDirectory(uploadFolderFile);
        File destinationFile = Paths.get(uploadDir + fileName).toFile();
        FileUtility.saveDownloadedBytesToFolder(fileBytes, destinationFile);
        // Get the pdf file from folder to return to client
        InputStreamResource resource = new InputStreamResource(new FileInputStream(uploadDir + fileName));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(Files.size(Paths.get(uploadDir + fileName)));
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        return new ChapterFile(headers, resource);
    }

    @SuppressWarnings("deprecation")
    public byte[] savePdfFromText(String myText, String fileOutputName) throws IOException {
        String url = "https://api.pdf.co/v1/pdf/convert/from/html";
        String apiKey = "laptrinhgalam123@gmail.com_9GFijyG3qTs3xsgOoYK08rflhCvca5kb408841Eb7YJkL4El08A2Sx8Y921NiHjS";
        JsonObject jsonBody = new JsonObject();
        jsonBody.add("html", new JsonPrimitive(myText));
        jsonBody.add("name", new JsonPrimitive(fileOutputName));
        jsonBody.add("margins", new JsonPrimitive("25px 50px 25px 50px"));
        jsonBody.add("paperSize", new JsonPrimitive("Letter"));
        jsonBody.add("orientation", new JsonPrimitive("Portrait"));
        jsonBody.add("printBackground", new JsonPrimitive(true));
        jsonBody.add("header", new JsonPrimitive(""));
        jsonBody.add("footer", new JsonPrimitive(""));
        jsonBody.add("mediaType", new JsonPrimitive("print"));
        jsonBody.add("async", new JsonPrimitive(false));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        OkHttpClient webClient = new OkHttpClient();
        Response response = webClient.newCall(request).execute();
        if (response.code() == 200) {
            // Parse JSON response
            JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
            boolean error = json.get("error").getAsBoolean();
            if (!error) {
                // Get URL of generated PDF file
                String resultFileUrl = json.get("url").getAsString();
                // Prepare request
                Request downloadFileRequest = new Request.Builder()
                        .url(resultFileUrl)
                        .build();
                // Execute request
                Response downloadFileResponse = webClient.newCall(downloadFileRequest).execute();
                byte[] fileBytes = downloadFileResponse.body().bytes();
                return fileBytes;
            } else {
                // Display service reported error
                throw new IOException(json.get("message").getAsString());
            }
        } else {
            // Display request error
            throw new HttpStatusException(response.code() + " " + response.message(), response.code(), url);
        } 
    }

}