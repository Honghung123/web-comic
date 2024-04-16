package com.group17.comic.plugins.concretes;
 
import java.net.URI; 
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse; 
import java.util.ArrayList;
import java.util.List; 
 
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.group17.comic.model.Author;
import com.group17.comic.model.Comic;
import com.group17.comic.model.DataModel;
import com.group17.comic.model.Genre;
import com.group17.comic.model.ComicModel;
import com.group17.comic.model.Pagination;
import com.group17.comic.plugins.IDocument;
import com.group17.comic.plugins.WebCrawler;
import com.group17.comic.utils.StringConverter;

public class TruyenFullCrawler implements WebCrawler, IDocument {
    private final String TRUYEN_API = "https://api.truyenfull.vn/";
    private final String TRUYEN_URL = "https://truyenfull.vn/"; 
    
    @Override
    public DataModel<List<ComicModel>> search(String keyword, int currentPage) { 
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "%20");
        String apiUrl = TRUYEN_API + "/v1/tim-kiem?title="+ term;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination pagination = null;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();

                // Sử dụng Gson để chuyển đổi JSON thành đối tượng truyện
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for(JsonElement element : jsonArray) {                    
                    String url = element.getAsJsonObject().get("id").getAsString();
                    String title = element.getAsJsonObject().get("title").getAsString();
                    String image = element.getAsJsonObject().get("image").getAsString();
                    String[] categories = element.getAsJsonObject().get("categories").getAsString().split("[,]");
                    List<Genre> genres = new ArrayList<>();
                    for (String untrimedCategory : categories) {
                        String category = untrimedCategory.trim();
                        // Chuyển Tiếng việt có dấu thành không dấu. Ví dụ: Đam Mỹ -> dam-my
                        String convertedCategory = StringConverter.removeDiacriticalMarks(category).toLowerCase().replaceAll(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    int newestChapter = element.getAsJsonObject().get("total_chapters").getAsInt();
                    String updatedTime = element.getAsJsonObject().get("time").getAsString();
                    listMatchedComic.add(new ComicModel(url, title, image, WebCrawler.alternateImage, genres, newestChapter, newestChapter, updatedTime));
                }
                var paginationObject = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObject.get("total").getAsInt();
                int perPage = paginationObject.get("per_page").getAsInt();
                int totalPages = paginationObject.get("total_pages").getAsInt();
                pagination = new Pagination(currentPage, perPage, totalPages, totalItems);
            } else {
                System.out.println("Failed to request. Error code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        
        DataModel<List<ComicModel>> result = new DataModel<>(pagination, listMatchedComic);
        return result;
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>(); 
        // Covert html to Document instance
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL);
        Elements elements = doc.select(".nav.navbar-nav li:nth-child(2) ul.dropdown-menu li a");
        for (Element element : elements) {
            String url = element.attr("href");
            String fullTagWithRedundantSlash = url.substring(url.lastIndexOf("the-loai"));
            String fullTag = fullTagWithRedundantSlash.substring(0, fullTagWithRedundantSlash.lastIndexOf("/"));
            String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
            String label = element.text();
            genres.add(new Genre(label, tag, fullTag));
        }
        return genres;
    }
    
    @Override
    public DataModel<List<ComicModel>> getLastedComics(int currentPage) {
        String apiUrl = TRUYEN_API + "v1/story/all?type=story_update&page=" + currentPage;
        List<ComicModel> lastedComics = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination pagination = null;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Sử dụng Gson để chuyển đổi JSON thành đối tượng truyện
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for(JsonElement element : jsonArray) {                    
                    String url = element.getAsJsonObject().get("id").getAsString();
                    String title = element.getAsJsonObject().get("title").getAsString();
                    String image = element.getAsJsonObject().get("image").getAsString();
                    String[] categories = element.getAsJsonObject().get("categories").getAsString().split("[,]");
                    List<Genre> genres = new ArrayList<>();
                    for (String untrimedCategory : categories) {
                        String category = untrimedCategory.trim();
                        // Chuyển Tiếng việt có dấu thành không dấu. Ví dụ: Đam Mỹ -> dam-my
                        String convertedCategory = StringConverter.removeDiacriticalMarks(category).toLowerCase().replaceAll(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    int newestChapter = element.getAsJsonObject().get("total_chapters").getAsInt();
                    String updatedTime = element.getAsJsonObject().get("time").getAsString();
                    lastedComics.add(new ComicModel(url, title, image, WebCrawler.alternateImage, genres, newestChapter, newestChapter, updatedTime));
                    var paginationOjbect = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                    int totalItems = paginationOjbect.get("total").getAsInt();
                    int perPage = paginationOjbect.get("per_page").getAsInt();
                    int totalPages = paginationOjbect.get("total_pages").getAsInt();
                    pagination = new Pagination(currentPage, perPage, totalPages, totalItems);
                }
            } else {
                System.out.println("Failed to request. Error code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        DataModel<List<ComicModel>> result = new DataModel<>(pagination, lastedComics);
        return result;
    }

    @Override
    public Comic getComicInfo(String urlComic) {
        int id = Integer.parseInt(urlComic);
        String apiUrl = TRUYEN_API + "v1/story/detail/" + id;
        System.out.println(apiUrl);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Comic comic = null;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();

                // Sử dụng Gson để chuyển đổi JSON thành đối tượng truyện
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class).getAsJsonObject("data");
                comic = Comic.builder()
                        .id(jsonObject.get("id").getAsInt())
                        .title(jsonObject.get("title").getAsString())
                        .image(jsonObject.get("image").getAsString())
                        .description(jsonObject.get("description").getAsString())
                        .author(new Author(null, jsonObject.get("author").getAsString()))
                        .tag(null)
                        .build();
            } else {
                System.out.println("Failed to request. Error code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comic;
    }
}
