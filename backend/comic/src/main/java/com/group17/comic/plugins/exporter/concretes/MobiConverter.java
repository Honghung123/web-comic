package com.group17.comic.plugins.exporter.concretes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.plugins.exporter.IFileConverter;
import com.group17.comic.utils.FileUtility;
import com.group17.comic.utils.StringUtility;
import io.swagger.v3.core.util.Json;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
@Slf4j
public class MobiConverter implements IFileConverter {
    private final UUID id = UUID.randomUUID();
    private final String uploadDir = "backend/comic/src/main/java/com/group17/comic/plugins/exporter/uploads/";
    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getPluginName() {
        return "MOBI";
    }

    @Override
    public String getBlobType() {
        return "application/x-mobipocket-ebook";
    }
    @SneakyThrows
    @Override
    public ChapterFile getConvertedFile(ChapterDTO chapterDto) {
        String formatTitile = StringUtility.removeDiacriticalMarks(chapterDto.title());
        formatTitile = formatTitile.replaceAll("[^a-zA-Z0-9]", "-").trim();
        String fileName = formatTitile + ".mobi";
        log.info(fileName);
        // Convert html to pdf online, and download it afterwards
        byte[] fileBytes = saveAsMobiFromText(chapterDto.content(), formatTitile + ".txt");
        if(fileBytes == null){
            log.info("File byte is null !!!!!!!!!!!");
        } else{
            log.info("File byte is not null !!!!!!");
        }
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
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/x-mobipocket-ebook"));
        return new ChapterFile(headers, resource);

    }
    public byte[] saveAsMobiFromText(String content, String fileInputName) throws IOException {
        final String API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiN2I3NGUyZjc3NTI0OTYwNzA0MGMxMWQ4MTIwNTZhNTIyMWY1NDkyNTEyOWYzMzQ2NTBiYWRiZGNhZjNjOGYwYTg2ZGEyM2Y1ZGNhMGFlZTkiLCJpYXQiOjE3MTY1NjMzOTIuMjEzNjA1LCJuYmYiOjE3MTY1NjMzOTIuMjEzNjA3LCJleHAiOjQ4NzIyMzY5OTIuMjA5ODU1LCJzdWIiOiI2ODQ4MDIwNiIsInNjb3BlcyI6WyJ1c2VyLnJlYWQiLCJ1c2VyLndyaXRlIiwidGFzay5yZWFkIiwidGFzay53cml0ZSIsIndlYmhvb2sucmVhZCIsIndlYmhvb2sud3JpdGUiLCJwcmVzZXQucmVhZCIsInByZXNldC53cml0ZSJdfQ.d8xv5FPsXoKxp7NoGe_IyqzwUhjraQNwIR07cioygczVJU2SV8UGksvKum4ljdzYaI7QaxEeOw4Ji91lR2wPgcCZcwadMYZieau1je-vzmY30U3Y2a3s0VjOokCUEvoXtciyFJZviO9zaWtv7-hHRDMOxYczw4EuNChs0RMPcBsBJJBRAgC9LN-npsViOsDlPvzvQTWZrtCZFWdIgOKJoL87M44J8mTKhFBWP2Ixw8nxkywsfm_cDHU7TPpudaDbdbe_cVVxN4V_VbAkYUwisR8LM7NhqvrzBnGK_ZNbVOYdGAnreDs4fyS1APWcXWyVsDP4cYhyOwwVodKOiBZhbfsAcLyFh2GHt1kC3e5ic44Dm6LxNTKS6sA-r1IsOxg8wGBm_zH-txo6PEK6c_ySi-ny9dZb5Qdit0fetASzzjkjjhjHgII-1JQkNj5SktkvSsoKxXJDrAT-Wmo8srybG_34n_rqAO9vI2rMQwbgg7_56RGoIAyeakKAW15RoF7Xy3SvC1kOl7x4AQ7tsSARgtSYhEI7j00zk9RgHiL_VkW9Ao9OCmrMZcrU1Z2L02kVtwOpdcPcRqlJBb8lA4-siGmt2OcAf2_z8rVGVFZIvE9GtwYnA1Ud70HQnG5YNFPgAuMF3F9scmZAdqrtPaqWvidy6RtG6dPECu7LLq1vgOw";
        final String BASE_URL = "https://api.cloudconvert.com/v2";
        String url = BASE_URL + "/jobs";
        byte[] fileBytes = null;
        JsonObject requestJson = new JsonObject();
        JsonObject tasks = new JsonObject();
        JsonObject importFile = new JsonObject();
        importFile.addProperty("operation", "import/raw");

        importFile.addProperty("file", content);
        importFile.addProperty("filename", fileInputName);
        tasks.add("import-file", importFile);

        JsonObject convertFile = new JsonObject();
        convertFile.addProperty("operation", "convert");
        convertFile.addProperty("input", "import-file");
        convertFile.addProperty("input_format", "txt");
        convertFile.addProperty("output_format", "mobi");
        convertFile.addProperty("page_range", "1-2");
        convertFile.addProperty("optimize_print", true);
        tasks.add("convert-file", convertFile);

        JsonObject exportFile = new JsonObject();
        exportFile.addProperty("operation", "export/url");
        exportFile.addProperty("input", "convert-file");
        tasks.add("export-file", exportFile);

        JsonObject requestBody = new JsonObject();
        requestBody.add("tasks", tasks);


        okhttp3.RequestBody body = okhttp3.RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        OkHttpClient webClient = new OkHttpClient();
        Response response = webClient.newCall(request).execute();
        if(response.code() == 201){
            JsonObject responseBody = new JsonParser().parse(response.body().string()).getAsJsonObject();
            JsonObject data = responseBody.get("data").getAsJsonObject();
            String id = data.get("id").getAsString();
            log.info("Id of the job" + id);
            request = new Request.Builder()
                    .url("https://sync.api.cloudconvert.com/v2/jobs/" + id)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .get()
                    .build();

            response = webClient.newCall(request).execute();
            String downloadUrl = "";
            if(response.code() == 200){
                responseBody = new JsonParser().parse(response.body().string()).getAsJsonObject();
                data = responseBody.get("data").getAsJsonObject();
                JsonArray tasksJson = data.getAsJsonArray("tasks");
                for (JsonElement taskElem : tasksJson) {
                    JsonObject task = taskElem.getAsJsonObject();
                    // We only want to process the "export-file" task.
                    if (task.get("name").getAsString().equals("export-file")) {
                        if (task.has("result")) {
                            JsonObject result = task.getAsJsonObject("result");
                            if (result.has("files")) {
                                JsonArray files = result.getAsJsonArray("files");
                                JsonObject file = files.get(0).getAsJsonObject();
                                if (file.has("url")) {
                                    downloadUrl = file.get("url").getAsString();

                                }
                            }
                        }
                    }
                }
            }
            request = new Request.Builder()
                    .url(downloadUrl)
                    .build();
            response = webClient.newCall(request).execute();
            fileBytes = response.body().bytes();
        }
        return fileBytes;
    }
}
