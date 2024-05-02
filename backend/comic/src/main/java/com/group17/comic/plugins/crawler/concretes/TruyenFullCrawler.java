package com.group17.comic.plugins.crawler.concretes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.group17.comic.dto.request.AlternatedChapterDTO;
import com.group17.comic.exception.customs.IllegalParameterException;
import com.group17.comic.exception.customs.InvalidTypeException;
import com.group17.comic.exception.customs.ResourceNotFound; 
import com.group17.comic.model.*; 
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.crawler.WebCrawler;
import com.group17.comic.utils.*;

import lombok.SneakyThrows;

public class TruyenFullCrawler extends WebCrawler implements IDataCrawler {
    private final String TRUYEN_API = "https://api.truyenfull.vn/";
    private final String TRUYEN_URL = "https://truyenfull.vn/"; 

    @Override
    public String getPluginName() {
        return "Truyen Full";
    }

    private Map<String, Integer> initialCategoryList() {
        Map<String, Integer> categories = new HashMap<>();
        var genreList = this.getGenres();
        int categoryId = 1;
        for (Genre genre : genreList) {
            categories.put(genre.getTag(), categoryId++);
        }
        return categories;     
    }

    private Integer getCategoryId(String genre) {
        Map<String, Integer> categories = initialCategoryList();
        if(categories.containsKey(genre)) {
            return categories.get(genre);
        }else {
            throw new InvalidTypeException("Invalid genre: " + genre);
        }
    }

    @SneakyThrows
    @Override
    public DataSearchModel<Integer, List<ComicModel>, List<Author>> search(String keyword,
            String byGenre, int currentPage) {
        if(StringUtils.hasLength(keyword) && StringUtils.hasLength(byGenre)) {
            return searchByKeywordAndGenre(keyword, byGenre, currentPage);
        }else if(keyword.isEmpty() && StringUtils.hasLength(byGenre)) {
            return searchOnlyByGenre(byGenre, currentPage);
        }else if(StringUtils.hasLength(keyword) && byGenre.isEmpty()) {
            return searchOnlyByKeyword(keyword, currentPage);
        }else {
            return this.getHotOrPromoteComics(currentPage);
        } 
    }

    @SneakyThrows
    private DataSearchModel<Integer, List<ComicModel>, List<Author>> searchByKeywordAndGenre(String keyword, String byGenre, int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "%20");
        Integer categoryId = this.getCategoryId(byGenre); 
        String apiUrl = TRUYEN_API + "v1/tim-kiem?title=" + term + "&category=[" + categoryId + "]&page=" + currentPage;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination = null; 
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body(); 
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject(); 
                    String comicTagId = jsonObj.get("id").getAsString();
                    String title = jsonObj.get("title").getAsString();
                    String image = jsonObj.get("image").getAsString();
                    String[] categories = jsonObj.get("categories").getAsString().split("[,]");
                    List<Genre> genres = new ArrayList<>();
                    for (String untrimedCategory : categories) {
                        String category = untrimedCategory.trim();
                        String convertedCategory = StringUtility.removeDiacriticalMarks(category).toLowerCase()
                                .replaceAll(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    String authorName = jsonObj.get("author").getAsString();
                    String authorId = StringUtility.removeDiacriticalMarks(authorName)
                                                    .toLowerCase().replace(" ", "-");
                    var author = new Author(authorId, authorName);
                    int newestChapter = jsonObj.get("total_chapters").getAsInt();
                    String updatedTime = jsonObj.get("time").getAsString();
                    boolean isFull = false;
                    var comicModel = ComicModel.builder()
                            .tagId(comicTagId)
                            .title(title)
                            .image(image)
                            .alternateImage(this.alternateImage)
                            .genres(genres)
                            .author(author)
                            .newestChapter(newestChapter)
                            .totalChapter(newestChapter)
                            .updatedTime(updatedTime)
                            .isFull(isFull)
                            .build();
                    listMatchedComic.add(comicModel);                    
                }
                var paginationObject = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObject.get("total").getAsInt();
                int perPage = paginationObject.get("per_page").getAsInt();
                int totalPages = paginationObject.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
            } else { 
                throw new ResourceNotFound("Failed to get data from TruyenFull.");
            }
        } catch (Exception e) {
            throw new HttpStatusException("Cannot make request to get data from TruyenFull", 500, apiUrl);
        }
        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, null);
        return result;
    }

    @SneakyThrows
    private DataSearchModel<Integer, List<ComicModel>, List<Author>> searchOnlyByGenre(String byGenre, int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>(); 
        String apiUrl = TRUYEN_API + "/v1/story/cate?cate="+ byGenre +"&type=story_new&page=" + currentPage;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination = null; 
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body(); 
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject(); 
                    String comicTagId = jsonObj.get("id").getAsString();
                    String title = jsonObj.get("title").getAsString();
                    String image = jsonObj.get("image").getAsString();
                    String[] categories = jsonObj.get("categories").getAsString().split("[,]");
                    List<Genre> genres = new ArrayList<>();
                    for (String untrimedCategory : categories) {
                        String category = untrimedCategory.trim();
                        String convertedCategory = StringUtility.removeDiacriticalMarks(category).toLowerCase()
                                .replaceAll(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    String authorName = jsonObj.get("author").getAsString();
                    String authorId = StringUtility.removeDiacriticalMarks(authorName)
                                                    .toLowerCase().replace(" ", "-");
                    var author = new Author(authorId, authorName);
                    int newestChapter = jsonObj.get("total_chapters").getAsInt();
                    String updatedTime = jsonObj.get("time").getAsString();
                    boolean isFull = false;
                    var comicModel = ComicModel.builder()
                            .tagId(comicTagId)
                            .title(title)
                            .image(image)
                            .alternateImage(this.alternateImage)
                            .genres(genres)
                            .author(author)
                            .newestChapter(newestChapter)
                            .totalChapter(newestChapter)
                            .updatedTime(updatedTime)
                            .isFull(isFull)
                            .build();
                    listMatchedComic.add(comicModel);                    
                }
                var paginationObject = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObject.get("total").getAsInt();
                int perPage = paginationObject.get("per_page").getAsInt();
                int totalPages = paginationObject.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
            } else { 
                throw new ResourceNotFound("Failed to get data from TruyenFull.");
            }
        } catch (Exception e) {
            throw new HttpStatusException("Cannot make request to get data from TruyenFull", 500, apiUrl);
        }
        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, null);
        return result;
    }

    @SneakyThrows
    private DataSearchModel<Integer, List<ComicModel>, List<Author>> searchOnlyByKeyword(String keyword, int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "%20");
        String apiUrl = TRUYEN_API + "/v1/tim-kiem?title=" + term + "&page=" + currentPage;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination = null;
        List<Author> authorList = new ArrayList<Author>();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body(); 
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject();
                    String title = jsonObj.get("title").getAsString();
                    String formatedTitle = StringUtility.removeDiacriticalMarks(title).toLowerCase();
                    String formatedKeyword = StringUtility.removeDiacriticalMarks(keyword).toLowerCase();
                    if (formatedTitle.contains(formatedKeyword)) {
                        String comicTagId = jsonObj.get("id").getAsString();
                        String image = jsonObj.get("image").getAsString();
                        String[] categories = jsonObj.get("categories").getAsString().split("[,]");
                        List<Genre> genres = new ArrayList<>();
                        for (String untrimedCategory : categories) {
                            String category = untrimedCategory.trim();
                            String convertedCategory = StringUtility.removeDiacriticalMarks(category).toLowerCase()
                                    .replaceAll(" ", "-");
                            genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                        }
                        String authorName = jsonObj.get("author").getAsString();
                        String authorId = StringUtility.removeDiacriticalMarks(authorName).toLowerCase().replace(" ",
                                "-");
                        var author = new Author(authorId, authorName);
                        int newestChapter = jsonObj.get("total_chapters").getAsInt();
                        String updatedTime = jsonObj.get("time").getAsString();
                        boolean isFull = false;
                        var comicModel = ComicModel.builder()
                                .tagId(comicTagId)
                                .title(title)
                                .image(image)
                                .alternateImage(this.alternateImage)
                                .genres(genres)
                                .author(author)
                                .newestChapter(newestChapter)
                                .totalChapter(newestChapter)
                                .updatedTime(updatedTime)
                                .isFull(isFull)
                                .build();
                        listMatchedComic.add(comicModel);
                    } else {
                        String authorName = jsonObj.get("author").getAsString();
                        String authorId = StringUtility.removeDiacriticalMarks(authorName).toLowerCase().replace(" ",
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
                throw new ResourceNotFound("Failed to get data from TruyenFull.");
            }
        } catch (Exception e) {
            throw new HttpStatusException("Cannot make request to get data from TruyenFull", 500, apiUrl);
        }
        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, authorList);
        return result;
    }

    @SneakyThrows
    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        // Covert html to Document instance
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL);
        Elements elements = doc.select(".nav.navbar-nav li:nth-child(2) ul.dropdown-menu li a");
        if(elements == null){
            throw new ResourceAccessException("Cannot get the genre list from Truyen full");
        }
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

    @SneakyThrows
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
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    String comicTagId = element.getAsJsonObject().get("id").getAsString();
                    String title = element.getAsJsonObject().get("title").getAsString();
                    String image = element.getAsJsonObject().get("image").getAsString();
                    String authorName =element.getAsJsonObject().get("author").getAsString();
                    String authorId = StringUtility.removeDiacriticalMarks(authorName).toLowerCase().replace(" ", "-");
                    Author author = new Author(authorId, authorName);
                    List<Genre> genres = new ArrayList<>();
                    String[] categories = element.getAsJsonObject().get("categories").getAsString().split("[,]");
                    for (String category : categories) {
                        category = category.trim(); 
                        String convertedCategory = StringUtility.removeDiacriticalMarks(category).toLowerCase()
                                .replaceAll(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    int newestChapter = element.getAsJsonObject().get("total_chapters").getAsInt();
                    String updatedTime = element.getAsJsonObject().get("time").getAsString();
                    boolean isFull = element.getAsJsonObject().get("is_full").getAsBoolean();
                    var comicModel = ComicModel.builder()
                            .tagId(comicTagId)
                            .title(title)
                            .image(image)
                            .alternateImage(this.alternateImage)
                            .genres(genres)
                            .author(author)
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
                throw new ResourceNotFound("Failed to get lasted chapter list from Truyen Full");
            }
        } catch (Exception e) {  
            throw new HttpStatusException("Failed to request to get lasted chapter list from Truyen Full", 500, apiUrl);
        }
        DataModel<Integer, List<ComicModel>> result = new DataModel<>(pagination, lastedComics);
        return result;
    }

    @SneakyThrows
    @Override
    public Comic getComicInfo(String comicTagId) {
        if(!comicTagId.matches("^\\d+$")) {
            throw new InvalidTypeException("Invalid comic tag id"); 
        }
        int id = Integer.parseInt(comicTagId); 
        String apiUrl = TRUYEN_API + "v1/story/detail/" + id;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Comic comic = null;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body(); 
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class).getAsJsonObject("data");
                String[] genresArray = jsonObject.get("categories").getAsString().split("[,]");
                String authorName = jsonObject.get("author").getAsString();
                String authorId = StringUtility.removeDiacriticalMarks(authorName).toLowerCase().replace(" ", "-");
                var genres = Arrays.asList(genresArray).stream().map(genre -> {
                    String category = genre.trim(); 
                    String convertedCategory = StringUtility.removeDiacriticalMarks(category).toLowerCase()
                            .replaceAll(" ", "-");
                    return new Genre(category, convertedCategory, "the-loai/" + convertedCategory);
                }).toList();
                comic = Comic.builder()
                        .tagId(jsonObject.get("id").getAsString())
                        .title(jsonObject.get("title").getAsString())
                        .image(jsonObject.get("image").getAsString())
                        .description(jsonObject.get("description").getAsString())
                        .author(new Author(authorId, authorName))
                        .genres(genres)
                        .alternateImage(this.alternateImage)
                        .build();
            } else {
                throw new ResourceNotFound("Failed to request comic information from TruyenFull");
            }
        } catch (Exception e) {
            throw new HttpStatusException("Cannot make request to get comic information from TruyenFull", 500, apiUrl);
        }
        return comic;
    }

    @SneakyThrows
    private DataSearchModel<Integer, List<ComicModel>, List<Author>> getHotOrPromoteComics(int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>(); 
        String apiUrl = TRUYEN_API + "/v1/story/all?type=story_full_rate&page=" + currentPage;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination = null; 
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body(); 
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject(); 
                    String comicTagId = jsonObj.get("id").getAsString();
                    String title = jsonObj.get("title").getAsString();
                    String image = jsonObj.get("image").getAsString();
                    String[] categories = jsonObj.get("categories").getAsString().split("[,]");
                    List<Genre> genres = new ArrayList<>();
                    for (String untrimedCategory : categories) {
                        String category = untrimedCategory.trim();
                        String convertedCategory = StringUtility.removeDiacriticalMarks(category).toLowerCase()
                                .replaceAll(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    String authorName = jsonObj.get("author").getAsString();
                    String authorId = StringUtility.removeDiacriticalMarks(authorName)
                                                    .toLowerCase().replace(" ", "-");
                    var author = new Author(authorId, authorName);
                    int newestChapter = jsonObj.get("total_chapters").getAsInt();
                    String updatedTime = jsonObj.get("time").getAsString();
                    boolean isFull = false;
                    var comicModel = ComicModel.builder()
                            .tagId(comicTagId)
                            .title(title)
                            .image(image)
                            .alternateImage(this.alternateImage)
                            .genres(genres)
                            .author(author)
                            .newestChapter(newestChapter)
                            .totalChapter(newestChapter)
                            .updatedTime(updatedTime)
                            .isFull(isFull)
                            .build();
                    listMatchedComic.add(comicModel);                    
                }
                var paginationObject = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObject.get("total").getAsInt();
                int perPage = paginationObject.get("per_page").getAsInt();
                int totalPages = paginationObject.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
            } else { 
                throw new ResourceNotFound("Failed to get data from TruyenFull.");
            }
        } catch (Exception e) {
            throw new HttpStatusException("Cannot make request to get data from TruyenFull", 500, apiUrl);
        }
        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, null);
        return result;
    }

    @Override
    @SneakyThrows
    public DataModel<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage) {
        if(!comicTagId.matches("^\\d+$")) {
            throw new InvalidTypeException("Invalid comic tag id"); 
        }
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
                throw new ResourceNotFound("Failed to get chapters from TruyenFull");
            }
        } catch (Exception e) { 
            throw new HttpStatusException("Failed to make request to get chapters from TruyenFull", 500, apiUrl);
        }
        DataModel<Integer, List<Chapter>> result = new DataModel<>(pagination, chapters);
        return result;
    }

    @Override
    @SneakyThrows
    public DataModel<Integer, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter) {
        if(!comicTagId.matches("^\\d+$")) { 
            throw new InvalidTypeException("Invalid comic tag id"); 
        }
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
                if (!jsonObject.get("chapter_prev").isJsonNull()) { 
                    pagination.setPreviousPage(jsonObject.get("chapter_prev").getAsInt());
                }
                if (!jsonObject.get("chapter_next").isJsonNull()) { 
                    pagination.setNextPage(jsonObject.get("chapter_next").getAsInt());
                }
                String title = jsonObject.get("story_name").getAsString();
                String content = jsonObject.get("content").getAsString();
                Author author = this.getAuthorOfComic(comicTagId);
                chapterContent = new ComicChapterContent(title, content, comicTagId, author);
            } else {
                throw new ResourceNotFound("Failed to get chapter content from TruyenFull");
            }
        } catch (Exception e) {
            throw new HttpStatusException("Failed to make request to get chapter content from TruyenFull", 500, apiUrl);
        }
        DataModel<Integer, ComicChapterContent> result = new DataModel<>(pagination, chapterContent);
        return result;
    }

    @SneakyThrows
    private Author getAuthorOfComic(String comicTagId) { 
        String apiUrl = TRUYEN_API + "v1/story/detail/" + comicTagId;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body(); 
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class).getAsJsonObject("data");
                String authorName = jsonObject.get("author").getAsString();
                String authorId = StringUtility.removeDiacriticalMarks(authorName).toLowerCase().replace(" ", "-");
                return new Author(authorId, authorName);
            } else {
                throw new ResourceNotFound("Failed to request comic information from TruyenFull");
            }
        } catch (Exception e) {
            throw new HttpStatusException("Cannot make request to get comic information from TruyenFull", 500, apiUrl);
        }
    }

    @Override
    @SneakyThrows
    public DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(AlternatedChapterDTO altChapterDto) {
        // AE tìm truyện cùng tên truyện và cùng tên tác giả, tìm chương có chapterNo chỉ định(ví dụ chương 4),
        // thì ae tìm chương chỉ chứa số 4 trả về chapter 4 và pagination - trang trước và trang kế tiếp 
        String keyword = altChapterDto.title();
        keyword = StringUtility.removeDiacriticalMarks(keyword).toLowerCase()
                .replace("[dich]", "").replaceAll("- suu tam", "");
        String term = keyword.substring(0, keyword.lastIndexOf("-")).trim().replace(" ", "%20");
        var formattedAuthor = StringUtility.removeDiacriticalMarks(altChapterDto.authorName()).toLowerCase().trim();
        String apiUrl = TRUYEN_API + "/v1/tim-kiem?title=" + term;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        String tagId = "";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body(); 
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject();
                    String title = jsonObj.get("title").getAsString();
                    String formatedTitle = StringUtility.removeDiacriticalMarks(title).toLowerCase();
                    if (StringUtility.findLongestCommonSubstring(formatedTitle, keyword).length() >= 0.5 * keyword.length()) {
                        String authorName = jsonObj.get("author").getAsString();
                        String authorFormattedName = StringUtility.removeDiacriticalMarks(authorName).toLowerCase().trim();
                        if(authorFormattedName.equals(formattedAuthor)) {
                            tagId = jsonObj.get("id").getAsString();
                            break;
                        }
                    } 
                }
            }
        } catch (Exception e) {
            throw new HttpStatusException("Failed to make request to get chapter content from TruyenFull", 500, apiUrl);
        }      
        String chapterUrl = ""; 
        int currentPage = 1;
        while(true){
            DataModel<Integer, List<Chapter>> result = this.getChapters(tagId, currentPage);
            List<Chapter> chapters = result.getData();
            if(chapters == null) {
                throw new ResourceNotFound("Failed to get chapter list from Truyen Full");
            }
            if(chapters.isEmpty()) {
                break;
            }
            for (Chapter chapter : chapters) {
                if(chapter.getTitle().contains(" " + String.valueOf(altChapterDto.chapterNo()) + ":")) {
                    chapterUrl = chapter.getChapterNo();
                    break;
                }
            }
            if(chapterUrl.length() > 0) {
                break;
            }
            currentPage++;
        }
        return this.getComicChapterContent(tagId, chapterUrl);     
    }

    @Override
    @SneakyThrows
    public DataModel<Integer, List<ComicModel>> getComicsByAuthor(String authorId, int currentPage) {
        List<ComicModel> lastedComics = new ArrayList<>();
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "tac-gia/" + authorId);
        Elements elements = doc.select(".col-truyen-main .list-truyen .row");
        if (elements == null) {
            throw new ResourceNotFound("Failed to get lasted chapters from Truyen Chu TH");
        }
        for (Element element : elements) {
            String image = element.selectFirst(".col-xs-3 div[data-image]").attr("data-image");
            var comicAnchor = element.selectFirst(".col-xs-7 .truyen-title a");
            String title = comicAnchor.text();
            String comicUrl = comicAnchor.attr("href");
            String tagId = comicUrl.substring(comicUrl.lastIndexOf("vn/") + 3, comicUrl.lastIndexOf("/")); 
            String authorName = element.selectFirst(".col-xs-7 .author span").text();
            var author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            boolean isFull = false;
            var comicModel = ComicModel.builder()
                    .tagId(tagId)
                    .title(title)
                    .image(image)
                    .alternateImage(this.alternateImage)
                    .genres(genres)
                    .author(author)
                    .isFull(isFull)
                    .build();
            lastedComics.add(comicModel);
        }
        int perPage = elements.size();
        Pagination<Integer> pagination = new Pagination<>(currentPage, perPage, 1, -1);
        DataModel<Integer, List<ComicModel>> result = new DataModel<>(pagination, lastedComics);
        return result;
    }
}
