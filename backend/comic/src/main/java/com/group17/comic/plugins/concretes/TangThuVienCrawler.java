package com.group17.comic.plugins.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;

import com.group17.comic.exception.ResourceNotFound;
import com.group17.comic.log.Logger;
import com.group17.comic.model.*;
import com.group17.comic.plugins.*;
import com.group17.comic.utils.*;

public class TangThuVienCrawler extends WebCrawler implements IDataCrawler {
    private final String TRUYEN_URL = "https://truyen.tangthuvien.vn/";

    @Override
    public DataSearchModel<Integer, List<ComicModel>, List<Author>> search(String keyword, int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "%20");
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "ket-qua-tim-kiem?term=" + term);
        Elements elements = doc.select("#rank-view-list .book-img-text ul li");
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
        List<Author> authorList = new ArrayList<>();
        var pagination = new Pagination<Integer>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        DataSearchModel<Integer, List<ComicModel>, List<Author>> dataDto = new DataSearchModel<>(pagination,
                listMatchedComic, authorList);
        return dataDto;
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<Genre>();
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL);
        Elements elements = doc.select("div#classify-list dd a");
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

    @Override
    public DataModel<Integer, List<ComicModel>> getLastedComics(int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "tong-hop?tp=cv&page=" + currentPage);

        List<ComicModel> lastedComics = new ArrayList<>();
        Elements elements = doc.select("div#rank-view-list ul li");
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

    @Override
    public Comic getComicInfo(String comicTagId) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/" + comicTagId);

        Element element = doc.selectFirst("div.book-information");
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
        if (description.contains("Bước 1")) {
            description = description.substring(0, description.indexOf("Bước 1") - 1);
        }
        var comic = Comic.builder()
                .tagId(comicTagId)
                .title(title)
                .image(image)
                .alternateImage(this.alternateImage)
                .description(description)
                .author(author)
                .genres(genres)
                .rate(rate)
                .isFull(false)
                .build();
        return comic;
    }

    @Override
    public DataModel<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/" + comicTagId);
        Pagination<Integer> pagination = null;
        List<Chapter> chapters = new ArrayList<>();
        var totalItemsText = doc.getElementById("j-bookCatalogPage").text();
        int totalItems = 0;
        var totalItemMatcher = Pattern.compile("\\((\\d+) chương\\)").matcher(totalItemsText);
        if (totalItemMatcher.find()) {
            totalItems = Integer.parseInt(totalItemMatcher.group(1));
        } else {
            Logger.logError(this.getClass().getSimpleName() + " Can't get total items in pagination",
                    new Exception("Can't get total items in pagination"));
        }
        int comicId = Integer.parseInt(doc.getElementById("story_id_hidden").val());
        Document chaperListDoc = this
                .getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/page/" + comicId + "?page=" + (currentPage - 1));
        Elements elements = chaperListDoc.select(".col-md-6 ul li a span");
        int perPage = elements.size();
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
        var paginationTag = chaperListDoc.select("ul.pagination li:last-child a");
        String totalPageText = paginationTag.attr("onclick");
        int totalPages = 0;
        Matcher totalPageMatcher = Pattern.compile("\\((\\d+)\\)").matcher(totalPageText);
        if (totalPageMatcher.find()) {
            totalPages = Integer.parseInt(totalPageMatcher.group(1)) + 1;
        } else {
            Logger.logError(this.getClass().getSimpleName() + " Can't get total items in pagination",
                    new Exception("Can't get total items in pagination"));
        }
        pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        DataModel<Integer, List<Chapter>> result = new DataModel<>(pagination, chapters);
        return result;
    }

    @Override
    public DataModel<Integer, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter) {
        Document doc = this
                .getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/" + comicTagId + "/chuong-" + currentChapter);
        var elementTitle = doc.selectFirst(".chapter-c-content h5 a");
        if (elementTitle == null) {
            Logger.logError(this.getClass().getSimpleName() + " Can't get chapter content",
                    new Exception("Can't get chapter content"));
            throw new ResourceNotFound(HttpStatus.NOT_FOUND, "Can't get chapter content");
        }
        String title = elementTitle.text().substring(elementTitle.text().lastIndexOf(":") + 1).trim();
        var elementContent = doc.selectFirst(".chapter-c-content .box-chap");
        if (elementContent == null) {
            Logger.logError(this.getClass().getSimpleName() + " Can't get chapter content",
                    new Exception("Can't get chapter content"));
            throw new ResourceNotFound(HttpStatus.NOT_FOUND, "Can't get chapter content");
        }
        String content = elementContent.html();
        Pagination<Integer> paginationTemp = getChapters(comicTagId, 1).getPagination();
        Pagination<Integer> pagination = new Pagination<>(Integer.parseInt(currentChapter), 1,
                paginationTemp.getTotalItems(), paginationTemp.getTotalItems());
        DataModel<Integer, ComicChapterContent> result = new DataModel<>(pagination,
                new ComicChapterContent(title, content, comicTagId));
        return result;
    }
}
