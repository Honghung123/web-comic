package com.group17.comic.plugins.exporter.concretes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group17.comic.dto.request.ChapterDTO;
import com.group17.comic.dto.response.ChapterFile;
import com.group17.comic.plugins.exporter.IFileConverter;
import com.group17.comic.utils.FileUtility;
import com.group17.comic.utils.StringUtility;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Slf4j
public class Azw3Converter implements IFileConverter {
    private final UUID id = UUID.randomUUID();
    private final String uploadDir = "backend/comic/src/main/java/com/group17/comic/plugins/exporter/uploads/";
    @Value("${comic.plugin.converter.api_key}")
    private String api_key;
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
    public ChapterFile getConvertedFile(ChapterDTO chapterDto) {
        String formatTitile = StringUtility.removeDiacriticalMarks(chapterDto.title());
        formatTitile = formatTitile.replaceAll("[^a-zA-Z0-9]", "-").trim();
        String fileName = formatTitile + ".azw3";
        // Convert txt to azw3 online, and download it afterwards
        byte[] fileBytes = saveAsAZW3FromText(chapterDto.content(), formatTitile + ".txt");
        // Then save the pdf file to folder

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
        // api_key = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiZWMyOGFlMmExZjlhMDhjMWZhNTYzN2NlOGMwMjUyMmEwYTA5YTIyNzI0Mjg0M2JjODdkMzM0ZWRmYTc0MTk1ZDQzMmJmY2FmNTMxNTcwYzQiLCJpYXQiOjE3MTY2MDMwNjcuNzQ0MDExLCJuYmYiOjE3MTY2MDMwNjcuNzQ0MDE0LCJleHAiOjQ4NzIyNzY2NjcuNzIzMzc1LCJzdWIiOiI2ODQ4NTY3NiIsInNjb3BlcyI6WyJ1c2VyLnJlYWQiLCJ1c2VyLndyaXRlIiwidGFzay5yZWFkIiwidGFzay53cml0ZSIsIndlYmhvb2sucmVhZCIsIndlYmhvb2sud3JpdGUiLCJwcmVzZXQucmVhZCIsInByZXNldC53cml0ZSJdfQ.ORRDLBHXHbA3bMc8Q5aBGWogBfnvTsvguO-QcA7X2zla8EzhG7mtURD8gtop2Np3KMjszay_bASs1B2iuEx1m3mYF5nt_yDkidSru94jt5NqL-UdbiEgrkYhf9iNGtJdH2bS4gGGvJKdSGnluH5waZ-jKwws8Vz87T8zZwlk9GVp1-Tlom_XLiMIgwV6WLFv4uuDRN4EnirgC4PjNh_zh6So0FDN7cw7QnYEtkpTW8S1VSZSzLkSjqdBPTf6PpuUj63YZdWVod1OveOavupEvJnYIH4k-w3KLaooGP31TOKT5nwZipV0lqLV5nAvnt58PDcdaz3o6oLh6lyHZ29n5XIEb5EuWouaSByvnULsrH4dplhCQmIdpc_r40UGDZ-DXxBcR60g_0SEEkUGlcziHkV7JLWRSRCChF4PJFOBpsPvrO2vomOBtLDw0KJPWYYCb1kaFAqzrttvbYV06R7g9e5UnQlQ5UuCwz-sgwNPIHa77oe-3lia7tJT4U4wS_ksurIiVHPjeSme7QsWSksY6abD5j47Ijj07AtsaDZ_8dallmVfFfU0DVHMeEK1VMmkBJsa7URDakxXdedfhWpdRhUcgxs85HvkYdYb7yINu4WH1BvNPes-4z4awVq9wGOuxhVN-OzGgTS1Do9R_BFg6VxnHmrlbrhIaPIa4uryo8s";
        api_key = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiYzE2M2VjZjk0OWI5YTdkZjBlNzE2ZTc4NGQ0YzYzMWYxNzE4NGRkMDgwNjM3MGVkNzViY2U0MDhkYzRlOWNjMTJiYzk3YTRlMTcyY2YzYzciLCJpYXQiOjE3MTY2Mjk3OTguMjkxMDM3LCJuYmYiOjE3MTY2Mjk3OTguMjkxMDM4LCJleHAiOjQ4NzIzMDMzOTguMjg3OTkyLCJzdWIiOiI2ODQ4Nzg2OSIsInNjb3BlcyI6WyJ1c2VyLnJlYWQiLCJ1c2VyLndyaXRlIiwidGFzay5yZWFkIiwidGFzay53cml0ZSJdfQ.Z3-C_e8VhVcu-S9s2Hndm7vAo6EF448MjJ86nD-8uzZf6IjsIPUp8_DoQL6S-rhwIa9jlnxhmJwfNIlSls1NmlWgEO-A5_m6yGYWybxFySXMFTtz_VFGZzj1YE8ks8TMQTcyuwew--HcdKuJmbvx7ZSsC0MvTOov4eB2GnwlVsQYHSaiXE8D2qwMk0W7Z538Ropki6gl5eyN-K-mdfK3VeKxsnoXCRtMLDoTIdD4NdUH7_lEm9mC_Y0K_39P1-8fu8UWQxSR2wXwWIT0y2DTi8dAKxgpPwNZRLGd1G0gsmeggh5yY6BF6mSCmd_S0uEzP8QRcO1n2QWZytMvdZ1DBXfkMlHQqWF-SeyUIwoF3_AN3ph5SrPB-3zwt0OE5Woz1dx-GT7-T_eyegLIEbKcvq-UkGpUNFFCON7GjtH7H7LRISfvAGA7AAMUEDMnvB-EFw_g9C6Nm6YIWX9qEoix9B5_NVmVKiItyrK0HgB_O0rU9-L6zTfX9DbzzrwxFvvaXJINr8tMkktLOt6jVXFmt0twI1_D5Dvls68uCa2Wwi5Lqrgrz1T-svYJVq3fOjYEfChUIoY-OFW1DbGCdDqZKWhHpPN8Rh1Zz0OYYbJ9PphQ43mEsB99lBRRBN0g0xU-xujKBJKyOht0aTZ_LsH_MWxJ18iSnkJMyoYRcJSiym0";
        final String BASE_URL = "https://api.cloudconvert.com/v2";
        String url = BASE_URL + "/jobs";
        byte[] fileBytes = null;
        JsonObject requestJson = new JsonObject();
        JsonObject tasks = new JsonObject();
        JsonObject importFile = new JsonObject();
        importFile.addProperty("operation", "import/raw");
        String contentTxt = StringUtility.removeHtmlTags(content);
        importFile.addProperty("file", contentTxt);
        importFile.addProperty("filename", fileInputName);
        tasks.add("import-file", importFile);

        JsonObject convertFile = new JsonObject();
        convertFile.addProperty("operation", "convert");
        convertFile.addProperty("input", "import-file");
        convertFile.addProperty("input_format", "txt");
        convertFile.addProperty("output_format", "azw3");
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
                .addHeader("Authorization", "Bearer " + api_key)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        OkHttpClient webClient = new OkHttpClient();
        Response response = webClient.newCall(request).execute();
        if(response.code() == 201){
            JsonObject responseBody = new JsonParser().parse(response.body().string()).getAsJsonObject();
            JsonObject data = responseBody.get("data").getAsJsonObject();
            String id = data.get("id").getAsString();
            request = new Request.Builder()
                    .url("https://sync.api.cloudconvert.com/v2/jobs/" + id)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + api_key)
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
