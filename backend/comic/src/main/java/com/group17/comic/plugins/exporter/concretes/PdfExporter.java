package com.group17.comic.plugins.exporter.concretes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.jsoup.HttpStatusException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.plugins.exporter.IFileExporter;
import com.group17.comic.utils.FileUtility;
import com.group17.comic.utils.StringUtility;

import lombok.SneakyThrows;
import okhttp3.*;

public class PdfExporter implements IFileExporter {
    private static final String UPLOAD_DIR = "backend/comic/src/main/java/com/group17/comic/plugins/exporter/uploads/";
    private static final UUID PLUGIN_ID = UUID.randomUUID();

    @Override
    public UUID getId() {
        return PLUGIN_ID;
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
        String uploadFolderAbsolutePath = Paths.get(UPLOAD_DIR).toAbsolutePath().toString();
        File uploadFolderFile = new File(uploadFolderAbsolutePath);
        FileUtility.deleteDirectory(uploadFolderFile);
        FileUtility.createDirectory(uploadFolderFile);
        File destinationFile = Paths.get(UPLOAD_DIR + fileName).toFile();
        FileUtility.saveDownloadedBytesToFolder(fileBytes, destinationFile);
        // Get the pdf file from folder to return to client
        InputStreamResource resource = new InputStreamResource(new FileInputStream(UPLOAD_DIR + fileName));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(Files.size(Paths.get(UPLOAD_DIR + fileName)));
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        return new ChapterFile(headers, resource);
    }

    @SuppressWarnings("deprecation")
    public byte[] savePdfFromText(String myText, String fileOutputName) throws IOException {
        String url = "https://api.pdf.co/v1/pdf/convert/from/html";
        String apiKey = "damhonghung321@gmail.com_L8fwL0i2clSmq3IB156t54WoC1nqn6VD7eLIpZ73H3I22h4ZrWYcPt34Iba69GeP";
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
                Request downloadFileRequest =
                        new Request.Builder().url(resultFileUrl).build();
                // Execute request
                Response downloadFileResponse =
                        webClient.newCall(downloadFileRequest).execute();
                return downloadFileResponse.body().bytes();
            } else {
                throw new IOException(json.get("message").getAsString());
            }
        } else {
            throw new HttpStatusException(response.code() + " " + response.message(), response.code(), url);
        }
    }
}
