package com.group17.comic.plugins.exporter.concretes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.plugins.exporter.IFileExporter;
import com.group17.comic.utils.FileUtility;
import com.group17.comic.utils.StringUtility;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Getter
@Setter
class Azw3Response {
    private long id;
    private String filename;
    @JsonProperty("source_format")
    private String sourceFormat;
    @JsonProperty("target_format")
    private String targetFormat;
    @JsonProperty("multi_output_files")
    private boolean multiOutputFiles;
}

@Getter
@Setter
class Azw3ResponseExtend extends Azw3Response {
    private String date_added;
    private String date_started;
    private String status;
}

@Getter
@Setter
class ConvertedResponse{
    private String url;
}

@Getter
@Setter
class Azw3Conversion {
    private Azw3Response conversion_data;
    private String success;
    private boolean awaiting_chunks;
}

public class Azw3Exporter implements IFileExporter {
    private final UUID id = UUID.randomUUID();
    private final String uploadDir = "backend/comic/src/main/java/com/group17/comic/plugins/exporter/uploads/";
    @Value("${comic.plugin.converter.api_key}")
    private String api_key;
    // private final String apiKey = "8QmXYtnup34hFyAL831icx3KDYHyUEJ9";
    private final String apiKey = "zLa4koLMvk0tKOeDxoZBQLhB7j8oEveU";
    private final String apiUrl = "https://api.mconverter.eu/v1/start_conversion.php";
    private final String targetFormat = "azw3";

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getPluginName() {
        return "AZW3";
    }

    @Override
    public String getBlobType() {
        return "application/vnd.amazon.mobi8-ebook";
    }

    @SneakyThrows
    @Override
    public ChapterFile getConvertedFile(ChapterRequest chapterDto) {
        String formatTitile = StringUtility.removeDiacriticalMarks(chapterDto.title());
        formatTitile = formatTitile.replaceAll("[^a-zA-Z0-9]", "-").trim();
        String fileName = formatTitile + ".azw3";
        // Save txt file to uploads folder
        String removedHtmlTags = StringUtility.removeHtmlTags(chapterDto.content());
        FileUtility.createFile(uploadDir + formatTitile + ".txt", removedHtmlTags);
        // Convert txt to azw3 online, and download it afterwards
        byte[] fileBytes = saveAsAZW3FromText(removedHtmlTags, formatTitile + ".txt");
        // Then save the azw3 file to folder
        String uploadFolderAbsolutePath = Paths.get(uploadDir).toAbsolutePath().toString();
        File uploadFolderFile = new File(uploadFolderAbsolutePath);
        FileUtility.deleteDirectory(uploadFolderFile);
        FileUtility.createDirectory(uploadFolderFile);
        File destinationFile = Paths.get(uploadDir + fileName).toFile();
        FileUtility.saveDownloadedBytesToFolder(fileBytes, destinationFile);
        // Get the azw3 file from folder to return to client
        InputStreamResource resource = new InputStreamResource(new FileInputStream(uploadDir + fileName));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentLength(Files.size(Paths.get(uploadDir + fileName)));
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.amazon.mobi8-ebook"));
        return new ChapterFile(headers, resource);

    }

    public byte[] saveAsAZW3FromText(String content, String fileInputName) throws Exception {
        var azw3Conversion = this.startTheConversion(content, fileInputName);
        this.trackCurrentProgress(azw3Conversion.getConversion_data().getId());
        return this.downloadConvertedFile(azw3Conversion.getConversion_data().getId());
    }

    private byte[] downloadConvertedFile(long converterId) throws Exception {
        OkHttpClient client = new OkHttpClient();
        var reqUrl = "https://api.mconverter.eu/v1/get_file.php";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", apiKey)
                .addFormDataPart("conversion_id", String.valueOf(converterId))
                .build();
        Request request = new Request.Builder()
                .url(reqUrl)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body().bytes();
        } else {
            // Handle the error
            System.err.println("Request failed: " + response);
            throw new Exception("Request failed");
        }
    }

    private void trackCurrentProgress(long converterId) throws Exception {
        OkHttpClient client = new OkHttpClient();
        var reqUrl = "https://api.mconverter.eu/v1/check_progress.php";
        while (true) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("api_key", apiKey)
                    .add("conversion_id", String.valueOf(converterId))
                    .build();
            Request request = new Request.Builder()
                    .url(reqUrl)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            var resBody = response.body().string();
            // Kiểm tra xem request có thành công không
            if (response.isSuccessful()) {
                if (resBody.contains("\"finished\"")) {
                    return;
                }
                Thread.sleep(1000);
                continue;
            } else {
                // Handle the error
                System.err.println("Request failed: " + response);
                throw new Exception("Request failed");
            }
        }
    }

    private Azw3Conversion startTheConversion(String content, String fileInputName) throws Exception {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", apiKey)
                .addFormDataPart("target_format", targetFormat)
                .addFormDataPart("source", fileInputName,
                        RequestBody.create(MediaType.parse("plain/txt"), new File(uploadDir + fileInputName)))
                .build();
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        if (response.isSuccessful()) {
            return new Gson().fromJson(res, Azw3Conversion.class);
        } else {
            // Handle the error
            System.err.println("Request failed: " + response);
            throw new Exception("Request failed");
        }
    }
}
