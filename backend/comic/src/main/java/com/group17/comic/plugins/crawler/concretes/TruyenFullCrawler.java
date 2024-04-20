package com.group17.comic.plugins.crawler.concretes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.group17.comic.log.Logger;
import com.group17.comic.model.*; 
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.crawler.WebCrawler;
import com.group17.comic.utils.*;

public class TruyenFullCrawler extends WebCrawler implements IDataCrawler {
    private final String TRUYEN_API = "https://api.truyenfull.vn/";
    private final String TRUYEN_URL = "https://truyenfull.vn/";

    @Override
    public String getPluginName() {
        return "Truyen Full";
    }

    @Override
    public DataSearchModel<Integer, List<ComicModel>, List<Author>> search(String keyword, int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "%20");
        String apiUrl = TRUYEN_API + "/v1/tim-kiem?title=" + term;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination = null;
        List<Author> authorList = new ArrayList<Author>();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();

                // Sử dụng Gson để chuyển đổi JSON thành đối tượng truyện
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject();
                    String title = jsonObj.get("title").getAsString();
                    String formatedTitle = StringConverter.removeDiacriticalMarks(title).toLowerCase();
                    String formatedKeyword = StringConverter.removeDiacriticalMarks(keyword).toLowerCase();
                    if (formatedTitle.contains(formatedKeyword)) {
                        String comicTagId = jsonObj.get("id").getAsString();
                        String image = jsonObj.get("image").getAsString();
                        String[] categories = jsonObj.get("categories").getAsString().split("[,]");
                        List<Genre> genres = new ArrayList<>();
                        for (String untrimedCategory : categories) {
                            String category = untrimedCategory.trim();
                            String convertedCategory = StringConverter.removeDiacriticalMarks(category).toLowerCase()
                                    .replaceAll(" ", "-");
                            genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                        }
                        int newestChapter = jsonObj.get("total_chapters").getAsInt();
                        String updatedTime = jsonObj.get("time").getAsString();
                        boolean isFull = false;
                        var comicModel = ComicModel.builder()
                                .tagId(comicTagId)
                                .title(title)
                                .image(image)
                                .alternateImage(this.alternateImage)
                                .genres(genres)
                                .author(null)
                                .newestChapter(newestChapter)
                                .totalChapter(newestChapter)
                                .updatedTime(updatedTime)
                                .isFull(isFull)
                                .build();
                        listMatchedComic.add(comicModel);
                    } else {
                        String authorName = jsonObj.get("author").getAsString();
                        String authorId = StringConverter.removeDiacriticalMarks(authorName).toLowerCase().replace(" ",
                                "-");
                        if (!authorList.stream().anyMatch(author -> author.getAuthorId().equals(authorId))) {
                            authorList.add(new Author(authorId, authorName));
                        }
                    }
                }
                var paginationObject = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObject.get("total").getAsInt();
                int perPage = paginationObject.get("per_page").getAsInt();
                int totalPages = paginationObject.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
            } else {
                System.out.println("Failed to request. Error code: " + response.statusCode());
            }
        } catch (Exception e) {
            Logger.logError(e.getMessage(), e);
        }

        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, authorList);
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
    public DataModel<Integer, List<ComicModel>> getLastedComics(int currentPage) {
        String apiUrl = TRUYEN_API + "v1/story/all?type=story_update&page=" + currentPage;
        List<ComicModel> lastedComics = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination = null;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Sử dụng Gson để chuyển đổi JSON thành đối tượng truyện
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    String comicTagId = element.getAsJsonObject().get("id").getAsString();
                    String title = element.getAsJsonObject().get("title").getAsString();
                    String image = element.getAsJsonObject().get("image").getAsString();
                    String[] categories = element.getAsJsonObject().get("categories").getAsString().split("[,]");
                    List<Genre> genres = new ArrayList<>();
                    for (String untrimedCategory : categories) {
                        String category = untrimedCategory.trim();
                        // Chuyển Tiếng việt có dấu thành không dấu. Ví dụ: Đam Mỹ -> dam-my
                        String convertedCategory = StringConverter.removeDiacriticalMarks(category).toLowerCase()
                                .replaceAll(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    int newestChapter = element.getAsJsonObject().get("total_chapters").getAsInt();
                    String updatedTime = element.getAsJsonObject().get("time").getAsString();
                    boolean isFull = false;
                    var comicModel = ComicModel.builder()
                            .tagId(comicTagId)
                            .title(title)
                            .image(image)
                            .alternateImage(this.alternateImage)
                            .genres(genres)
                            .author(null)
                            .newestChapter(newestChapter)
                            .totalChapter(newestChapter)
                            .updatedTime(updatedTime)
                            .isFull(isFull)
                            .build();
                    lastedComics.add(comicModel);
                }
                var paginationOjbect = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationOjbect.get("total").getAsInt();
                int perPage = paginationOjbect.get("per_page").getAsInt();
                int totalPages = paginationOjbect.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
            } else {
                System.out.println("Failed to request. Error code: " + response.statusCode());
            }
        } catch (Exception e) {
            Logger.logError(e.getMessage(), e);
        }
        DataModel<Integer, List<ComicModel>> result = new DataModel<>(pagination, lastedComics);
        return result;
    }

    @Override
    public Comic getComicInfo(String comicTagId) {
        int id = Integer.parseInt(comicTagId);
        String apiUrl = TRUYEN_API + "v1/story/detail/" + id;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Comic comic = null;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();

                // Sử dụng Gson để chuyển đổi JSON thành đối tượng truyện
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class).getAsJsonObject("data");
                String[] genresArray = jsonObject.get("categories").getAsString().split("[,]");
                var genres = Arrays.asList(genresArray).stream().map(genre -> {
                    String category = genre.trim();
                    // Chuyển Tiếng việt có dấu này không dấu. Ví dụ: Đam Mỹ -> dam-my
                    String convertedCategory = StringConverter.removeDiacriticalMarks(category).toLowerCase()
                            .replaceAll(" ", "-");
                    return new Genre(category, convertedCategory, "the-loai/" + convertedCategory);
                }).toList();
                comic = Comic.builder()
                        .tagId(jsonObject.get("id").getAsString())
                        .title(jsonObject.get("title").getAsString())
                        .image(jsonObject.get("image").getAsString())
                        .description(jsonObject.get("description").getAsString())
                        .author(new Author(null, jsonObject.get("author").getAsString()))
                        .genres(genres)
                        .alternateImage(this.alternateImage)
                        .build();
            } else {
                System.out.println("Failed to request. Error code: " + response.statusCode());
            }
        } catch (Exception e) {
            Logger.logError(e.getMessage(), e);
        }
        return comic;
    }

    @Override
    public DataModel<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage) {
        int id = Integer.parseInt(comicTagId);
        String apiUrl = TRUYEN_API + "v1/story/detail/" + id + "/chapters?page=" + currentPage;
        Pagination<Integer> pagination = null;
        List<Chapter> chapters = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                var paginationObj = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObj.get("total").getAsInt();
                int perPage = paginationObj.get("per_page").getAsInt();
                int totalPages = paginationObj.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
                JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    JsonObject chapterObject = element.getAsJsonObject();
                    String chapter = chapterObject.get("id").getAsString();
                    String title = chapterObject.get("title").getAsString();
                    chapters.add(new Chapter(chapter, title));
                }
            } else {
                Logger.logError("Failed to request. Error code: " + response.statusCode(),
                        new Exception("Failed to request. Error code: " + response.statusCode()));
            }
        } catch (Exception e) {
            Logger.logError(e.getMessage(), e);
        }
        DataModel<Integer, List<Chapter>> result = new DataModel<>(pagination, chapters);
        return result;
    }

    @Override
    public DataModel<Integer, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter) {
        Integer chapterId = Integer.parseInt(currentChapter);
        String apiUrl = TRUYEN_API + "v1/chapter/detail/" + currentChapter;
        Pagination<Integer> pagination = null;
        ComicChapterContent chapterContent = null;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class).getAsJsonObject("data");
                pagination = new Pagination<>(chapterId, 1, 1, 1);
                if (jsonObject.get("chapter_prev").isJsonNull()) {
                    pagination.setPreviousPage(chapterId);
                } else {
                    pagination.setPreviousPage(jsonObject.get("chapter_prev").getAsInt());
                }
                if (jsonObject.get("chapter_next").isJsonNull()) {
                    pagination.setNextPage(chapterId);
                } else {
                    pagination.setNextPage(jsonObject.get("chapter_next").getAsInt());
                }

                String title = jsonObject.get("story_name").getAsString();
                String content = jsonObject.get("content").getAsString();
                chapterContent = new ComicChapterContent(title, content, comicTagId);
            } else {
                Logger.logError("Failed to request. Error code: " + response.statusCode(),
                        new Exception("Failed to request. Error code: " + response.statusCode()));
            }
        } catch (Exception e) {
            Logger.logError(e.getMessage(), e);
        }
        DataModel<Integer, ComicChapterContent> result = new DataModel<>(pagination, chapterContent);
        return result;
    }
}
