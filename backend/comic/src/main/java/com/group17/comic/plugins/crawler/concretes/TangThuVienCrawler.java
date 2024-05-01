package com.group17.comic.plugins.crawler.concretes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import com.group17.comic.dto.request.AlternatedChapterDTO;
import com.group17.comic.exception.customs.IllegalParameterException;
import com.group17.comic.exception.customs.InvalidTypeException;
import com.group17.comic.exception.customs.ResourceNotFound;
import com.group17.comic.log.Logger;
import com.group17.comic.model.*;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.crawler.WebCrawler;
import com.group17.comic.utils.*;

import lombok.SneakyThrows;

public class TangThuVienCrawler extends WebCrawler implements IDataCrawler {
    private final String TRUYEN_URL = "https://truyen.tangthuvien.vn/";

    @Override
    public String getPluginName() {
        return "Tang Thu Vien";
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
        if (categories.containsKey(genre)) {
            return categories.get(genre);
        } else {
            throw new InvalidTypeException("Invalid genre: " + genre);
        }
    }

    @SneakyThrows
    @Override
    public DataSearchModel<Integer, List<ComicModel>, List<Author>> search(String keyword,
            String byGenre, int currentPage) {
        if (StringUtils.hasLength(keyword) && StringUtils.hasLength(byGenre)) {
            return searchByKeywordAndGenre(keyword, byGenre, currentPage);
        } else if (keyword.isEmpty() && StringUtils.hasLength(byGenre)) {
            return searchOnlyByGenre(byGenre, currentPage);
        } else if (StringUtils.hasLength(keyword) && byGenre.isEmpty()) {
            return searchOnlyByKeyword(keyword, currentPage);
        } else {
            throw new IllegalParameterException("Invalid search params");
        }
    }

    @SneakyThrows
    private DataSearchModel<Integer, List<ComicModel>, List<Author>> searchByKeywordAndGenre(String keyword,
            String byGenre, int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String term = StringUtility.removeDiacriticalMarks(keyword).trim().toLowerCase();
        for (int page = 1; page <= 10; page++) {
            try {
                var comicsByGenres = this.searchOnlyByGenre(byGenre, page);
                comicsByGenres.getData().forEach(comic -> {
                    var formatedTitle = StringUtility.removeDiacriticalMarks(comic.getTitle()).trim().toLowerCase();
                    if (formatedTitle.contains(term)) {
                        listMatchedComic.add(comic);
                    }
                });
            } catch (Exception e) {
                continue;
            }
        }
        var pagination = new Pagination<Integer>(currentPage, listMatchedComic.size(), 1, -1);
        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, null);
        return result;
    }

    @SneakyThrows
    private DataSearchModel<Integer, List<ComicModel>, List<Author>> searchOnlyByGenre(String byGenre,
            int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        Integer categoryId = this.getCategoryId(byGenre);
        Document doc = this
                .getDocumentInstanceFromUrl(TRUYEN_URL + "tong-hop?tp=cv&ctg=" + categoryId + "&page=" + currentPage);
        Elements elements = doc.select("#rank-view-list .book-img-text ul li");
        if (elements == null) { // null khac voi 0 nha
            throw new ResourceNotFound("Failed to get comic list from Tang Thu Vien");
        }
        for (Element element : elements) {
            String image = element.selectFirst(".book-img-box a img").attr("src");
            String comicUrl = element.selectFirst(".book-mid-info h4 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".book-mid-info h4 a").text();
            var authorTag = element.select(".book-mid-info .author a:nth-of-type(1)");
            String authorName = authorTag.text();
            String authorUrl = authorTag.attr("href");
            String authorId = authorUrl.substring(authorUrl.lastIndexOf("/") + 1);
            Author author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            var genreTag = element.select(".book-mid-info .author a:nth-of-type(2)");
            String fullTag = genreTag.attr("href").substring(genreTag.attr("href").lastIndexOf("the-loai"));
            String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
            String label = genreTag.text();
            genres.add(new Genre(label, tag, fullTag));
            var chapterTag = element.select(".book-mid-info .author span span.KIBoOgno");
            int totalChapter = Integer.parseInt(chapterTag.text());
            String updatedTime = element.selectFirst(".book-mid-info .update span").text();
            boolean isFull = false;
            var comicModel = ComicModel.builder()
                    .tagId(comicTagId)
                    .title(title)
                    .image(image)
                    .alternateImage(this.alternateImage)
                    .genres(genres)
                    .author(author)
                    .totalChapter(totalChapter)
                    .totalChapter(totalChapter)
                    .updatedTime(updatedTime)
                    .isFull(isFull)
                    .build();
            listMatchedComic.add(comicModel);
        }
        var perPage = elements.size();
        var lastAnchorTag = doc.select("ul.pagination li:nth-last-child(2) a");
        int totalPages = 1;
        int totalItems = perPage;
        if (lastAnchorTag.size() == 1) {
            totalPages = Integer.parseInt(lastAnchorTag.text());
            totalItems = totalPages * perPage;
        }
        var pagination = new Pagination<Integer>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, null);
        return result;
    }

    @SneakyThrows
    private DataSearchModel<Integer, List<ComicModel>, List<Author>> searchOnlyByKeyword(String keyword,
            int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "%20");
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "ket-qua-tim-kiem?term=" + term);
        Elements elements = doc.select("#rank-view-list .book-img-text ul li");
        if (elements == null) { // null khac voi 0 nha
            throw new ResourceNotFound("Failed to get comic list from Tang Thu Vien");
        }
        for (Element element : elements) {
            String image = element.selectFirst(".book-img-box a img").attr("src");
            String comicUrl = element.selectFirst(".book-mid-info h4 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".book-mid-info h4 a").text();
            var authorTag = element.select(".book-mid-info .author a:nth-of-type(1)");
            String authorName = authorTag.text();
            String authorUrl = authorTag.attr("href");
            String authorId = authorUrl.substring(authorUrl.lastIndexOf("=") + 1);
            Author author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            var genreTag = element.select(".book-mid-info .author a:nth-of-type(2)");
            String fullTag = genreTag.attr("href").substring(genreTag.attr("href").lastIndexOf("the-loai"));
            String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
            String label = genreTag.text();
            genres.add(new Genre(label, tag, fullTag));
            var chapterTag = element.select(".book-mid-info .author span span.KIBoOgno");
            int totalChapter = Integer.parseInt(chapterTag.text());
            String updatedTime = element.selectFirst(".book-mid-info .update span").text();
            boolean isFull = false;
            var comicModel = ComicModel.builder()
                    .tagId(comicTagId)
                    .title(title)
                    .image(image)
                    .alternateImage(this.alternateImage)
                    .genres(genres)
                    .author(author)
                    .totalChapter(totalChapter)
                    .totalChapter(totalChapter)
                    .updatedTime(updatedTime)
                    .isFull(isFull)
                    .build();
            listMatchedComic.add(comicModel);
        }
        var perPage = elements.size();
        var lastAnchorTag = doc.select("ul.pagination li:nth-last-child(2) a");
        int totalPages = 1;
        int totalItems = perPage;
        if (lastAnchorTag.size() == 1) {
            totalPages = Integer.parseInt(lastAnchorTag.text());
            totalItems = totalPages * perPage;
        }
        List<Author> authorList = new ArrayList<>();
        var pagination = new Pagination<Integer>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, authorList);
        return result;
    }

    @SneakyThrows
    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<Genre>();
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL);
        Elements elements = doc.select("div#classify-list dd a");
        if (elements == null) {
            throw new ResourceNotFound("Failed to get genre list from Tang Thu Vien");
        }
        for (Element element : elements) {
            String url = element.attr("href");
            if (url.contains("the-loai")) {
                String fullTag = url.substring(url.lastIndexOf("the-loai"));
                String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
                String label = element.select("span.info i").text();
                genres.add(new Genre(label, tag, fullTag));
            }
        }
        return genres;
    }

    @SneakyThrows
    @Override
    public DataModel<Integer, List<ComicModel>> getLastedComics(int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "tong-hop?tp=cv&page=" + currentPage);
        List<ComicModel> lastedComics = new ArrayList<>();
        Elements elements = doc.select("div#rank-view-list ul li");
        if (elements == null) {
            throw new ResourceNotFound("Failed to get lasted comics from Tang Thu Vien");
        }
        for (Element element : elements) {
            String image = element.selectFirst(".book-img-box a img").attr("src");
            String comicUrl = element.selectFirst(".book-mid-info h4 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".book-mid-info h4 a").text();
            var authorTag = element.select(".book-mid-info .author a:nth-of-type(1)");
            String authorName = authorTag.text();
            String authorUrl = authorTag.attr("href");
            String authorId = authorUrl.substring(authorUrl.lastIndexOf("/") + 1);
            Author author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            var genreTag = element.select(".book-mid-info .author a:nth-of-type(2)");
            String fullTag = genreTag.attr("href").substring(genreTag.attr("href").lastIndexOf("the-loai"));
            String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
            String label = genreTag.text();
            genres.add(new Genre(label, tag, fullTag));
            var chapterTag = element.select(".book-mid-info .author span span.KIBoOgno");
            int totalChapter = Integer.parseInt(chapterTag.text());
            String updatedTime = element.selectFirst(".book-mid-info .update span").text();
            var comicModel = ComicModel.builder()
                    .tagId(comicTagId)
                    .title(title)
                    .image(image)
                    .alternateImage(this.alternateImage)
                    .genres(genres)
                    .author(author)
                    .totalChapter(totalChapter)
                    .newestChapter(totalChapter)
                    .updatedTime(updatedTime)
                    .build();
            lastedComics.add(comicModel);
        }
        int perPage = elements.size();
        var lastAnchorTag = doc.select("ul.pagination li:nth-last-of-type(2) a");
        int totalPages = 1;
        int totalItems = perPage;
        if (lastAnchorTag.size() == 1) {
            totalPages = Integer.parseInt(lastAnchorTag.text());
            totalItems = totalPages * perPage;
        }
        var pagination = new Pagination<Integer>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        DataModel<Integer, List<ComicModel>> result = new DataModel<>(pagination, lastedComics);
        return result;
    }

    @SneakyThrows
    @Override
    public Comic getComicInfo(String comicTagId) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/" + comicTagId);
        Element element = doc.selectFirst("div.book-information");
        if (element == null) {
            throw new ResourceNotFound("Failed to get chapter content from Tang Thu Vien");
        }
        String image = element.select(".book-img img").attr("src");
        String title = element.selectFirst(".book-info h1").text();
        var authorTag = element.select(".book-info .tag a:nth-of-type(1)");
        String authorId = authorTag.attr("href").substring(authorTag.attr("href").lastIndexOf("=") + 1);
        String authorName = authorTag.text();
        var author = new Author(authorId, authorName);
        Double rate = Double.parseDouble(element.select("cite#myrate").text());

        List<Genre> genres = new ArrayList<>();
        var genreTag = element.select(".book-info .tag a:nth-of-type(2)");
        String label = genreTag.text();
        String genreUrl = genreTag.attr("href");
        String fullTag = genreUrl.substring(genreUrl.lastIndexOf("the-loai"));
        String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
        genres.add(new Genre(label, tag, fullTag));

        Element descriptionElement = doc.selectFirst(".book-info-detail > .book-intro");
        String description = descriptionElement.html();
        Element statusElement = doc.selectFirst(".book-info .tag span.blue");
        boolean isFull = StringUtility.removeDiacriticalMarks(statusElement.text()).equals("Da hoan thanh");
        var comic = Comic.builder()
                .tagId(comicTagId)
                .title(title)
                .image(image)
                .alternateImage(this.alternateImage)
                .description(description)
                .author(author)
                .genres(genres)
                .rate(rate)
                .isFull(isFull)
                .build();
        return comic;
    }

    @SneakyThrows
    @Override
    public DataModel<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/" + comicTagId);
        Pagination<Integer> pagination = null;
        List<Chapter> chapters = new ArrayList<>();
        String totalItemsText = doc.getElementById("j-bookCatalogPage").text();
        int totalItems = 0;
        var totalItemMatcher = Pattern.compile("\\((\\d+) chương\\)").matcher(totalItemsText);
        if (totalItemMatcher.find()) {
            totalItems = Integer.parseInt(totalItemMatcher.group(1)) - 5;
        } else {
            Logger.logError(this.getClass().getSimpleName()
                    + " Can't get total items", null);
        }
        int comicId = Integer.parseInt(doc.getElementById("story_id_hidden").val());
        Document chaperListDoc = this
                .getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/page/" + comicId + "?page=" + (currentPage - 1));
        Elements elements = chaperListDoc.select(".col-md-6 ul li a span");
        for (Element element : elements) {
            String chapterNo = "";
            String title = "";
            var chapterMatcher = Pattern.compile("Chương\\s*(\\d+)\\s*:\\s*(.*)").matcher(element.text());
            if (chapterMatcher.find()) {
                chapterNo = chapterMatcher.group(1);
                title = chapterMatcher.group(2);
                chapters.add(new Chapter(chapterNo, title));
            } else {
                Logger.logError(this.getClass().getSimpleName() + " Can't get chapter number and title",
                        new Exception("Can't get chapter number"));
            }
        }
        int perPage = elements.size();
        int totalPages = totalItems / perPage + (totalItems % perPage == 0 ? 0 : 1);
        pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        DataModel<Integer, List<Chapter>> result = new DataModel<>(pagination, chapters);
        return result;
    }

    @SneakyThrows
    @Override
    public DataModel<Integer, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter) {
        Document doc = this
                .getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/" + comicTagId + "/chuong-" + currentChapter);
        var elementTitle = doc.selectFirst(".chapter-c-content h5 a");
        if (elementTitle == null) {
            throw new ResourceNotFound("Can't get chapter content from Tang Thu Vien");
        }
        String title = elementTitle.text().substring(elementTitle.text().lastIndexOf(":") + 1).trim();
        var elementContent = doc.selectFirst(".chapter-c-content .box-chap");
        if (elementContent == null) {
            throw new ResourceNotFound("Can't get chapter content from Tang Thu Vien");
        }
        String content = elementContent.html();
        Author author = this.getAuthorOfComic(comicTagId);
        Pagination<Integer> paginationTemp = getChapters(comicTagId, 1).getPagination();
        Pagination<Integer> pagination = new Pagination<>(Integer.parseInt(currentChapter), 1,
                paginationTemp.getTotalItems(), paginationTemp.getTotalItems());
        PaginationUtility.updatePagination(pagination);
        DataModel<Integer, ComicChapterContent> result = new DataModel<>(pagination,
                new ComicChapterContent(title, content, comicTagId, author));
        return result;
    }

    @SneakyThrows
    private Author getAuthorOfComic(String comicTagId) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/" + comicTagId);
        Element element = doc.selectFirst("div.book-information");
        if (element == null) {
            throw new ResourceNotFound("Failed to get chapter content from Tang Thu Vien");
        }
        var authorTag = element.select(".book-info .tag a:nth-of-type(1)");
        String authorId = authorTag.attr("href").substring(authorTag.attr("href").lastIndexOf("=") + 1);
        String authorName = authorTag.text();
        return new Author(authorId, authorName);
    }

    @SneakyThrows
    @Override
    public DataModel<?, ComicChapterContent> getComicChapterContentOnOtherServer(AlternatedChapterDTO altChapterDto) {
        // AE tìm truyện cùng tên truyện và cùng tên tác giả, tìm chương có chapterNo
        // chỉ định(ví dụ chương 4),
        // thì ae tìm chương chỉ chứa số 4 trả về chapter 4 và pagination - trang trước
        // và trang kế tiếp
        // Tìm truyện chứa tên và cùng tác giả
        String keyword = altChapterDto.title();
        keyword = StringUtility.removeDiacriticalMarks(keyword).toLowerCase()
                .replace("[dich]", "").replaceAll("- suu tam", "");
        keyword = keyword.substring(0, keyword.lastIndexOf("-")).trim().replace(" ", "%20");
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "ket-qua-tim-kiem?term=" + keyword);
        var formattedAuthor = StringUtility.removeDiacriticalMarks(altChapterDto.authorName()).toLowerCase().trim();
        Elements comicElements = doc.select("#rank-view-list .book-img-text ul li");
        if (comicElements == null) {
            throw new ResourceNotFound("Failed to get comic list from Tang Thu Vien");
        }
        String tagId = "";
        for (Element element : comicElements) {
            String comicUrl = element.selectFirst(".book-mid-info h4 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            var authorTag = element.select(".book-mid-info .author a:nth-of-type(1)");
            String authorName = authorTag.text();
            String authorFormattedName = StringUtility.removeDiacriticalMarks(authorName).toLowerCase();
            String commonTag = StringUtility.findLongestCommonSubstring(comicTagId, altChapterDto.comicTagId());
            if (authorFormattedName.equals(formattedAuthor) &&
                    commonTag.length() >= 0.5 * altChapterDto.comicTagId().length()) {
                tagId = comicTagId;
                break;
            }
        }
        if (tagId.isEmpty()) {
            throw new ResourceNotFound("Failed to get comic tag id from Tang Thu Vien");
        }
        // Tìm chapter
        String chapterUrl = "";
        int currentPage = 1;
        while (true) {
            DataModel<Integer, List<Chapter>> result = this.getChapters(tagId, currentPage);
            List<Chapter> chapters = result.getData();
            if (chapters == null) {
                throw new ResourceNotFound("Failed to get chapter list from Tang Thu Vien");
            }
            if (chapters.isEmpty()) {
                break;
            }
            for (Chapter chapter : chapters) {
                if (chapter.getChapterNo().contains(String.valueOf(altChapterDto.chapterNo()))) {
                    chapterUrl = chapter.getChapterNo();
                    break;
                }
            }
            if (chapterUrl.length() > 0) {
                break;
            }
            currentPage++;
        }
        return this.getComicChapterContent(tagId, chapterUrl);
    }

    @Override
    @SneakyThrows
    public DataModel<Integer, List<ComicModel>> getComicsByAuthor(String authorId, int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "tac-gia?author=" + authorId);
        List<ComicModel> lastedComics = new ArrayList<>();
        Elements elements = doc.select("div#rank-view-list ul li");
        if (elements == null) {
            throw new ResourceNotFound("Failed to get lasted comics from Tang Thu Vien");
        }
        for (Element element : elements) {
            String image = element.selectFirst(".book-img-box a img").attr("src");
            String comicUrl = element.selectFirst(".book-mid-info h4 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".book-mid-info h4 a").text();
            var authorTag = element.select(".book-mid-info .author a:nth-of-type(1)");
            String authorName = authorTag.text(); 
            Author author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            var genreTag = element.select(".book-mid-info .author a:nth-of-type(2)");
            String fullTag = genreTag.attr("href").substring(genreTag.attr("href").lastIndexOf("the-loai"));
            String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
            String label = genreTag.text();
            genres.add(new Genre(label, tag, fullTag));
            var chapterTag = element.select(".book-mid-info .author span span.KIBoOgno");
            int totalChapter = Integer.parseInt(chapterTag.text());
            String updatedTime = element.selectFirst(".book-mid-info .update span").text();
            var comicModel = ComicModel.builder()
                    .tagId(comicTagId)
                    .title(title)
                    .image(image)
                    .alternateImage(this.alternateImage)
                    .genres(genres)
                    .author(author)
                    .totalChapter(totalChapter)
                    .newestChapter(totalChapter)
                    .updatedTime(updatedTime)
                    .build();
            lastedComics.add(comicModel);
        }
        int perPage = elements.size();
        // var lastAnchorTag = doc.select("ul.pagination li:nth-last-of-type(2) a");
        // int totalPages = 1;
        // int totalItems = perPage;
        // if (lastAnchorTag.size() == 1) {
        //     totalPages = Integer.parseInt(lastAnchorTag.text());
        //     totalItems = totalPages * perPage;
        // }
        var pagination = new Pagination<Integer>(currentPage, perPage, 1, -1);
        // PaginationUtility.updatePagination(pagination);
        DataModel<Integer, List<ComicModel>> result = new DataModel<>(pagination, lastedComics);
        return result;    
    }
}
