package com.group17.comic.plugins.crawler.concretes;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import com.group17.comic.dto.request.AlternatedChapterDTO;
import com.group17.comic.exception.customs.IllegalParameterException;
import com.group17.comic.exception.customs.ResourceNotFound;
import com.group17.comic.model.*;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.crawler.WebCrawler;
import com.group17.comic.utils.*;

import lombok.SneakyThrows;

public class TruyenChuTHCrawler extends WebCrawler implements IDataCrawler {
    private final String TRUYEN_URL = "https://truyenchuth.com/";

    @Override
    public String getPluginName() {
        return "Truyen Chu TH";
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
                var comicsByGenre = this.searchOnlyByGenre(byGenre, page);
                comicsByGenre.getData().forEach(comic -> {
                    var formatedTitle = StringUtility.removeDiacriticalMarks(comic.getTitle()).trim().toLowerCase();
                    if (formatedTitle.contains(term)) {
                        listMatchedComic.add(comic);
                    }
                });
            } catch (Exception e) {
                continue;
            }
        }
        Pagination<Integer> pagination = new Pagination<Integer>(currentPage, listMatchedComic.size(), 1, -1);
        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, null);
        return result;
    }

    @SneakyThrows
    private DataSearchModel<Integer, List<ComicModel>, List<Author>> searchOnlyByGenre(String byGenre,
            int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String requestUrl = TRUYEN_URL + "loadmore?type=Theloai&cat=" + byGenre + "&page=" + currentPage;
        Document doc = this.getDocumentInstanceFromUrl(requestUrl);
        Elements comicElements = doc.select(".list-row-img");
        if (comicElements == null) {
            throw new ResourceNotFound("Failed to get comic list from Truyen Chu TH");
        }
        for (Element element : comicElements) {
            String image = element.selectFirst(".row-image a img").attr("src");
            String comicUrl = element.selectFirst(".row-info h3 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".row-info h3 a").text();
            String authorName = element.select(".row-author").text();
            String authorId = StringUtility.removeDiacriticalMarks(authorName).trim()
                    .toLowerCase().replaceAll(" ", "-");
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
        Pagination<Integer> pagination = new Pagination<Integer>(currentPage, listMatchedComic.size(), 1, -1);
        PaginationUtility.updatePagination(pagination);
        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, null);
        return result;
    }

    @SneakyThrows
    private DataSearchModel<Integer, List<ComicModel>, List<Author>> searchOnlyByKeyword(String keyword,
            int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "+");
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "searching?key=" + term);
        Elements authorElements = doc.select(".list-author .list-content .author h3 a");
        if (authorElements == null) {
            throw new ResourceNotFound("Failed to get author list from Truyen Chu TH");
        }
        List<Author> authorList = new ArrayList<>();
        for (Element authorElement : authorElements) {
            String authorId = authorElement.attr("href").substring(authorElement.attr("href").lastIndexOf("/") + 1);
            String authorName = authorElement.text();
            authorList.add(new Author(authorId, authorName));
        }
        Elements comicElements = doc.select(".list-story .list-content .list-row-img");
        if (comicElements == null) {
            throw new ResourceNotFound("Failed to get comic list from Truyen Chu TH");
        }
        for (Element element : comicElements) {
            String image = element.selectFirst(".row-image a img").attr("src");
            String comicUrl = element.selectFirst(".row-info h3 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".row-info h3 a").text();
            String authorName = element.select(".row-author").text();
            String authorId = StringUtility.removeDiacriticalMarks(authorName).trim()
                    .toLowerCase().replaceAll(" ", "-");
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
        Pagination<Integer> pagination = new Pagination<Integer>(currentPage, listMatchedComic.size(), 1, -1);
        DataSearchModel<Integer, List<ComicModel>, List<Author>> result = new DataSearchModel<>(pagination,
                listMatchedComic, authorList);
        return result;
    }

    @SneakyThrows
    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<Genre>();
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL);
        Elements elements = doc.select(".sidebar-content ul li b a");
        if (elements == null) {
            throw new ResourceNotFound("Failed to get genre list from Truyen Chu TH");
        }
        for (Element element : elements) {
            String url = element.attr("href");
            String tag = url.substring(url.lastIndexOf("/") + 1);
            String fullTag = "the-loai/" + tag;
            String label = element.text();
            genres.add(new Genre(label, tag, fullTag));
        }
        return genres;
    }

    @SneakyThrows
    @Override
    public DataModel<Integer, List<ComicModel>> getLastedComics(int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "loadmore?p=" + currentPage);
        List<ComicModel> lastedComics = new ArrayList<>();
        Elements elements = doc.select(".list-row-img");
        if (elements == null) {
            throw new ResourceNotFound("Failed to get lasted chapters from Truyen Chu TH");
        }
        for (Element element : elements) {
            var anchorTag = element.selectFirst(".row-info a");
            String title = anchorTag.text();
            String comicUrl = anchorTag.attr("href");
            String tagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String image = element.selectFirst(".row-image a img").attr("src");
            var authorTag = element.selectFirst(".row-author");
            String authorName = authorTag.text();
            String authorId = StringUtility.removeDiacriticalMarks(authorName).toLowerCase().replaceAll(" ", "-");
            Author author = new Author(authorId, authorName);
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

    @SneakyThrows
    @Override
    public Comic getComicInfo(String comicTagId) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + comicTagId);
        Element element = doc.getElementById("list");
        if (element.selectFirst(".detail") == null) {
            throw new ResourceNotFound("Failed to get chapter content from Truyen Chu TH");
        }
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

    @SneakyThrows
    @Override
    public DataModel<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + comicTagId + "?p=" + currentPage);
        Pagination<Integer> pagination = null;
        List<Chapter> chapters = new ArrayList<>();
        Elements elements = doc.select("#divtab ul li h4 a");
        if (elements == null) {
            throw new ResourceNotFound("Failed to get chapter list from Truyen Chu TH");
        }
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
        var lastestChapterText = doc.selectFirst("#newchaps ul li:last-child").text();
        int lastestChapter = Integer.parseInt(
                lastestChapterText.substring(lastestChapterText.indexOf(" ") + 1, lastestChapterText.indexOf(":")));
        int totalItems = lastestChapter;
        pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        DataModel<Integer, List<Chapter>> result = new DataModel<>(pagination, chapters);
        return result;
    }

    @SneakyThrows
    @Override
    public DataModel<String, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + comicTagId + "/" + currentChapter);
        var elementTitle = doc.selectFirst(".chapter-header ul li:nth-of-type(3) h3");
        if (elementTitle == null) {
            throw new ResourceNotFound("Can't get chapter title from Truyen Chu TH");
        }
        String title = elementTitle.text();
        title = title.substring(title.indexOf(":") + 1).trim();
        var elementContent = doc.selectFirst("#content");
        if (elementContent == null) {
            throw new ResourceNotFound("Can't get chapter content from Truyen Chu TH");
        }
        String content = elementContent.html();
        var authorElement = doc.selectFirst(".chapter-header ul li:nth-of-type(2) h3 a");
        String authorName= authorElement.text();
        String authorUrl = authorElement.attr("href");
        String authorId = authorUrl.substring(authorUrl.lastIndexOf("/") + 1);
        var author = new Author(authorId, authorName); 
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
                new ComicChapterContent(title, content, comicTagId, author));
        return result;
    }

    @SneakyThrows
    @Override
    public DataModel<String, ComicChapterContent> getComicChapterContentOnOtherServer(
            AlternatedChapterDTO altChapterDto) {
        // Tìm truyện chứa tên và cùng tác giả
        String keyword = altChapterDto.title();
        keyword = StringUtility.removeDiacriticalMarks(keyword).toLowerCase()
                .replace("[dich]", "").replaceAll("- suu tam", "");
        keyword = keyword.substring(0, keyword.lastIndexOf("-")).trim().replace(" ", "+");
        var formattedAuthor = StringUtility.removeDiacriticalMarks(altChapterDto.authorName()).toLowerCase().trim();
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "searching?key=" + keyword);
        Elements comicElements = doc.select(".list-story .list-content .list-row-img");
        if (comicElements == null) {
            throw new ResourceNotFound("Failed to get comic list from Truyen Chu TH");
        }
        String tagId = "";
        for (Element element : comicElements) {
            String comicUrl = element.selectFirst(".row-info h3 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            // String title = element.selectFirst(".row-info h3 a").text();
            String authorName = element.select(".row-author").text();
            String authorFormattedName = StringUtility.removeDiacriticalMarks(authorName).toLowerCase();
            String commonTag = StringUtility.findLongestCommonSubstring(comicTagId, altChapterDto.comicTagId());
            if (authorFormattedName.equals(formattedAuthor) &&
                    commonTag.length() >= 0.5 * altChapterDto.comicTagId().length()) {
                tagId = comicTagId;
                break;
            }
        }
        if (tagId.isEmpty()) {
            throw new ResourceNotFound("Failed to get comic tag id from Truyen Chu TH");
        }
        // Tìm chapter
        String chapterUrl = "";
        int currentPage = 1;
        while (true) {
            DataModel<Integer, List<Chapter>> result = this.getChapters(tagId, currentPage);
            List<Chapter> chapters = result.getData();
            if (chapters == null) {
                throw new ResourceNotFound("Failed to get chapter list from Truyen Chu TH");
            }
            if (chapters.isEmpty()) {
                break;
            }
            for (Chapter chapter : chapters) {
                if (chapter.getTitle().contains(" " + String.valueOf(altChapterDto.chapterNo()) + ":")) {
                    chapterUrl = chapter.getChapterNo();
                    break;
                }
            }
            if (chapterUrl.length() > 0) {
                break;
            }
            currentPage++;
        }
        // Lấy nội dung sau khi lấy được chapter và trả về
        return this.getComicChapterContent(tagId, chapterUrl);
    }

    @Override
    @SneakyThrows
    public DataModel<Integer, List<ComicModel>> getComicsByAuthor(String authorId, int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "tac-gia/" + authorId);
        List<ComicModel> lastedComics = new ArrayList<>();
        Elements elements = doc.select(".list-row-img");
        if (elements == null) {
            throw new ResourceNotFound("Failed to get lasted chapters from Truyen Chu TH");
        }
        for (Element element : elements) {
            var anchorTag = element.selectFirst(".row-info a");
            String title = anchorTag.text();
            String comicUrl = anchorTag.attr("href");
            String tagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String image = element.selectFirst(".row-image a img").attr("src");
            var authorTag = element.selectFirst(".row-author");
            String authorName = authorTag.text();
            Author author = new Author(authorId, authorName);
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
        int totalPages = 1;
        int totalItems = -1;
        var pagination = new Pagination<Integer>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        DataModel<Integer, List<ComicModel>> result = new DataModel<>(pagination, lastedComics);
        return result;
    }
}
