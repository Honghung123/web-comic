package com.group17.comic.plugins.concretes;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;

import com.group17.comic.exception.ResourceNotFound;
import com.group17.comic.log.Logger;
import com.group17.comic.model.*;
import com.group17.comic.plugins.*;
import com.group17.comic.utils.*;

public class TruyenChuTHCrawler extends WebCrawler implements IDataCrawler {
    private final String TRUYEN_URL = "https://truyenchuth.com/";

    @Override
    public DataSearchModel<Integer, List<ComicModel>, List<Author>> search(String keyword, int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "+");
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "searching?key=" + term);
        Elements authorElements = doc.select(".list-author .list-content .author h3 a");
        List<Author> authorList = new ArrayList<>();
        for (Element authorElement : authorElements) {
            String authorId = authorElement.attr("href").substring(authorElement.attr("href").lastIndexOf("/") + 1);
            String authorName = authorElement.text();
            authorList.add(new Author(authorId, authorName));
        }
        Elements comicElements = doc.select(".list-story .list-content .list-row-img");
        for (Element element : comicElements) {
            String image = element.selectFirst(".row-image a img").attr("src");
            String comicUrl = element.selectFirst(".row-info h3 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".row-info h3 a").text();
            String authorName = element.select(".row-author").text();
            String authorId = StringConverter.removeDiacriticalMarks(authorName).toLowerCase().replaceAll(" ", "-");
            Author author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            boolean isFull = false;
            var comicModel = ComicModel.builder()
                    .tagId(comicTagId)
                    .title(title)
                    .image(image)
                    .alternateImage(this.alternateImage)
                    .genres(genres)
                    .author(author)
                    .isFull(isFull)
                    .build();
            listMatchedComic.add(comicModel);
        }

        Pagination<Integer> pagination = null;
        DataSearchModel<Integer, List<ComicModel>, List<Author>> dataDto = new DataSearchModel<>(pagination,
                listMatchedComic, authorList);
        return dataDto;
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<Genre>();
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL);
        Elements elements = doc.select(".sidebar-content ul li b a");
        for (Element element : elements) {
            String url = element.attr("href");
            String tag = url.substring(url.lastIndexOf("/") + 1);
            String fullTag = "the-loai/" + tag;
            String label = element.text();
            genres.add(new Genre(label, tag, fullTag));
        }
        return genres;
    }

    @Override
    public DataModel<Integer, List<ComicModel>> getLastedComics(int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "loadmore?p=" + currentPage);
        List<ComicModel> lastedComics = new ArrayList<>();
        Elements elements = doc.select(".list-row-img");
        for (Element element : elements) {
            var anchorTag = element.selectFirst(".row-info a");
            Author author = new Author("", "");
            String title = anchorTag.text();
            String comicUrl = anchorTag.attr("href");
            String tagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String image = element.selectFirst(".row-image a img").attr("src");
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
        int totalPages = 25;
        int totalItems = totalPages * perPage;
        var pagination = new Pagination<Integer>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        DataModel<Integer, List<ComicModel>> result = new DataModel<>(pagination, lastedComics);
        return result;
    }

    @Override
    public Comic getComicInfo(String comicTagId) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + comicTagId);
        Element element = doc.getElementById("list");
        String image = element.select(".detail-thumbnail img").attr("src");
        String title = element.selectFirst(".detail-right h2 a").text();
        var authorTag = element.selectFirst(".detail-info ul li:nth-of-type(1) h2 a");
        var authorHref = authorTag.attr("href");
        String authorId = authorHref.substring(authorHref.lastIndexOf("/") + 1);
        String authorName = authorTag.text();
        var author = new Author(authorId, authorName);
        double rate = 0;
        List<Genre> genres = new ArrayList<>();
        var genreElement = element.selectFirst(".detail-info ul li:nth-of-type(2) a");
        String url = genreElement.attr("href");
        String tag = url.substring(url.lastIndexOf("/") + 1);
        String fullTag = "the-loai/" + tag;
        String label = genreElement.text();
        genres.add(new Genre(label, tag, fullTag));
        Element descriptionElement = doc.selectFirst(".summary article");
        String description = descriptionElement.html();
        boolean isFull = false;
        var comic = Comic.builder()
                .tagId(comicTagId)
                .title(title)
                .image(image)
                .alternateImage(this.alternateImage)
                .genres(genres)
                .author(author)
                .description(description)
                .rate(rate)
                .isFull(isFull)
                .build();
        return comic;
    }

    @Override
    public DataModel<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + comicTagId + "?p=" + currentPage);
        Pagination<Integer> pagination = null;
        List<Chapter> chapters = new ArrayList<>();
        Elements elements = doc.select("#divtab ul li h4 a");
        for (Element element : elements) {
            String comicUrl = element.attr("href");
            String chapterNo = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.text();
            chapters.add(new Chapter(chapterNo, title));
        }
        int perPage = elements.size();
        var lastPageTag = doc.select(".pagination ul.paging li.last a");
        var lastPageHref = lastPageTag.attr("href");
        int totalPages = Integer.parseInt(lastPageHref.substring(lastPageHref.lastIndexOf("=") + 1));
        int totalItems = perPage * totalPages;
        pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        DataModel<Integer, List<Chapter>> result = new DataModel<>(pagination, chapters);
        return result;
    }

    @Override
    public DataModel<String, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + comicTagId + "/" + currentChapter);
        var elementTitle = doc.selectFirst(".chapter-header ul li:nth-of-type(3) h3");
        if (elementTitle == null) {
            Logger.logError(this.getClass().getSimpleName() + " Can't get chapter content",
                    new Exception("Can't get chapter content"));
            throw new ResourceNotFound(HttpStatus.NOT_FOUND, "Can't get chapter content");
        }
        String title = elementTitle.text();
        var elementContent = doc.selectFirst("#content p");
        if (elementContent == null) {
            Logger.logError(this.getClass().getSimpleName() + " Can't get chapter content",
                    new Exception("Can't get chapter content"));
            throw new ResourceNotFound(HttpStatus.NOT_FOUND, "Can't get chapter content");
        }
        String content = elementContent.html();
        var nextChapElement = doc.getElementById("nextchap");
        var prevChapElement = doc.getElementById("prevchap");
        Pagination<String> pagination = new Pagination<>(currentChapter, 1, -1, -1);
        if (prevChapElement != null) {
            String prevPage = prevChapElement.attr("href");
            prevPage = prevPage.substring(prevPage.lastIndexOf("/") + 1);
            pagination.setPreviousPage(prevPage);
        }
        if (nextChapElement != null) {
            var nextPage = nextChapElement.attr("href");
            nextPage = nextPage.substring(nextPage.lastIndexOf("/") + 1);
            pagination.setNextPage(nextPage);
        }
        DataModel<String, ComicChapterContent> result = new DataModel<>(pagination,
                new ComicChapterContent(title, content, comicTagId));
        return result;
    }
}
