package com.group17.comic.plugins.exporter.concretes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.plugins.exporter.IFileExporter;
import com.group17.comic.utils.StringUtility;

import lombok.SneakyThrows;

public class Mp3Exporter implements IFileExporter {
    private static final UUID PLUGIN_ID = UUID.randomUUID();

    @Override
    public UUID getId() {
        return PLUGIN_ID;
    }

    @Override
    public String getPluginName() {
        return "MP3";
    }

    @Override
    public String getBlobType() {
        return "audio/mp3";
    }

    @SneakyThrows
    @Override
    public ChapterFile getConvertedFile(ChapterRequest chapterDto) {
        String formatTitile = StringUtility.removeDiacriticalMarks(chapterDto.title());
        formatTitile = formatTitile.replaceAll("[^a-zA-Z0-9]", "-").trim();
        String fileName = formatTitile + ".mp3";
        var mp3File = getMp3FromText(chapterDto.content());
        byte[] fileContent = readFileToByteArray(mp3File);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileContent));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileName + ".mp3")
                .build());
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("audio/mpeg"));
        return new ChapterFile(headers, resource);
    }

    public InputStream getMp3FromText(String content) throws IOException {
        String apiUrl = "https://viettelgroup.ai/voice/api/tts/v1/rest/syn";
        String voice = "hcm-diemmy";
        String tokenId = "YmYWNSjtm-8l3qrj0X-KvMyL5BWF1DjvZ6Va9c7YXCioOMsyQM2QXJ5E737Hn2nL";
        Gson gson = new Gson();
        // Tạo JSON body dùng JsonObject và Gson
        JsonObject jsonBody = new JsonObject();
        var removeHtmlTagContent = StringUtility.removeHtmlTags(content);
        jsonBody.addProperty("text", removeHtmlTagContent);
        jsonBody.addProperty("voice", voice);
        jsonBody.addProperty("id", "2");
        jsonBody.addProperty("without_filter", false);
        jsonBody.addProperty("speed", 1.0);
        jsonBody.addProperty("tts_return_option", 3);
        String datajson = gson.toJson(jsonBody);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(apiUrl);
        StringEntity body = new StringEntity(datajson, "UTF-8");
        /*add content-type, token into header request */
        request.addHeader("content-type", "application/json;charset=UTF-8");
        request.addHeader("token", tokenId);
        request.getRequestLine();
        request.setEntity(body);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

    private byte[] readFileToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
