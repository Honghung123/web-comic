package com.group17.comic.plugins.exporter.concretes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.enums.ExceptionType;
import com.group17.comic.exceptions.BusinessException;
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
class ConvertedResponse {
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
    private static final UUID PLUGIN_ID = UUID.randomUUID();
    private static final String UPLOAD_DIR = "backend/comic/src/main/java/com/group17/comic/plugins/exporter/uploads/";
    private static final String API_KEY = "zLa4koLMvk0tKOeDxoZBQLhB7j8oEveU";
    private static final String API_URL = "https://api.mconverter.eu/v1/start_conversion.php";
    private static final String TARGET_FORMAT = "azw3";

    @Override
    public UUID getId() {
        return PLUGIN_ID;
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
        FileUtility.createFile(UPLOAD_DIR + formatTitile + ".txt", removedHtmlTags);
        // Convert txt to azw3 online, and download it afterwards
        byte[] fileBytes = saveAsAZW3FromText(formatTitile + ".txt");
        // Then save the azw3 file to folder
        String uploadFolderAbsolutePath = Paths.get(UPLOAD_DIR).toAbsolutePath().toString();
        File uploadFolderFile = new File(uploadFolderAbsolutePath);
        FileUtility.deleteDirectory(uploadFolderFile);
        FileUtility.createDirectory(uploadFolderFile);
        File destinationFile = Paths.get(UPLOAD_DIR + fileName).toFile();
        FileUtility.saveDownloadedBytesToFolder(fileBytes, destinationFile);
        // Get the azw3 file from folder to return to client
        InputStreamResource resource = new InputStreamResource(new FileInputStream(UPLOAD_DIR + fileName));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentLength(Files.size(Paths.get(UPLOAD_DIR + fileName)));
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.amazon.mobi8-ebook"));
        return new ChapterFile(headers, resource);
    }

    public byte[] saveAsAZW3FromText(String fileInputName) throws Exception {
        var azw3Conversion = this.startTheConversion(fileInputName);
        this.trackCurrentProgress(azw3Conversion.getConversion_data().getId());
        return this.downloadConvertedFile(azw3Conversion.getConversion_data().getId());
    }

    private byte[] downloadConvertedFile(long converterId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        var reqUrl = "https://api.mconverter.eu/v1/get_file.php";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("conversion_id", String.valueOf(converterId))
                .build();
        Request request = new Request.Builder().url(reqUrl).post(requestBody).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body().bytes();
        } else {
            throw new BusinessException(ExceptionType.GET_CONVERTED_FILE_FAILED);
        }
    }

    private void trackCurrentProgress(long converterId) throws IOException, InterruptedException {
        OkHttpClient client = new OkHttpClient();
        var reqUrl = "https://api.mconverter.eu/v1/check_progress.php";
        while (true) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("api_key", API_KEY)
                    .add("conversion_id", String.valueOf(converterId))
                    .build();
            Request request =
                    new Request.Builder().url(reqUrl).post(requestBody).build();
            Response response = client.newCall(request).execute();
            var resBody = response.body().string();
            // Kiểm tra xem request có thành công không
            if (response.isSuccessful()) {
                if (resBody.contains("\"finished\"")) {
                    return;
                }
                Thread.sleep(1000);
            } else {
                throw new BusinessException(ExceptionType.TRACK_CONVERT_PROGRESS_FAILED);
            }
        }
    }

    private Azw3Conversion startTheConversion(String fileInputName) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("target_format", TARGET_FORMAT)
                .addFormDataPart(
                        "source",
                        fileInputName,
                        RequestBody.create(MediaType.parse("plain/txt"), new File(UPLOAD_DIR + fileInputName)))
                .build();
        Request request = new Request.Builder().url(API_URL).post(requestBody).build();
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        if (response.isSuccessful()) {
            return new Gson().fromJson(res, Azw3Conversion.class);
        } else {
            throw new BusinessException(ExceptionType.GET_CONVERTED_FILE_FAILED);
        }
    }
}
