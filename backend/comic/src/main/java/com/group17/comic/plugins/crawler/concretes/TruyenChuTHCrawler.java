package com.group17.comic.plugins.crawler.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.group17.comic.enums.ExceptionType;
import com.group17.comic.exceptions.BusinessException;
import com.group17.comic.log.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import com.group17.comic.dtos.request.AlternatedChapterRequest;
import com.group17.comic.dtos.response.AuthorResponse;
import com.group17.comic.models.*;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.crawler.WebCrawler;
import com.group17.comic.utils.*;

import lombok.SneakyThrows;

public class TruyenChuTHCrawler extends WebCrawler implements IDataCrawler {
    private static final String COMIC_BASE_URL = "https://truyenchuth.com/";
    private static final UUID PLUGIN_ID =  UUID.fromString("123e4567-e89b-12d3-a456-426614173001");
    @Override
    public UUID getID() {
        return PLUGIN_ID;
    }

    @Override
    public String getPluginName() {
        return "Truyen Chu TH";
    }

    @SneakyThrows
    @Override
    public SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> search(String keyword,
                                                                                          String byGenre, int currentPage) {
        keyword = StringUtility.removeDiacriticalMarks(keyword);
        if (StringUtils.hasLength(keyword) && StringUtils.hasLength(byGenre)) {
            return searchByKeywordAndGenre(keyword, byGenre, currentPage);
        } else if (keyword.isEmpty() && StringUtils.hasLength(byGenre)) {
            return searchOnlyByGenre(byGenre, currentPage);
        } else if (StringUtils.hasLength(keyword) && byGenre.isEmpty()) {
            return searchOnlyByKeyword(keyword, currentPage);
        } else {
            return this.getHotOrPromoteComics(currentPage);
        }
    }

    @SneakyThrows
    private SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> searchByKeywordAndGenre(String keyword,
                                                                                                            String byGenre, int currentPage) {
        List<LatestComic> listMatchedComic = new ArrayList<>();
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
                Logger.logError("Error in getting this comic", e);
            }
        }
        Pagination<Integer> pagination = new Pagination<>(currentPage, listMatchedComic.size(), 1, -1);
        return new SearchingPageableData<>(pagination, listMatchedComic, null);
    }

    @SneakyThrows
    private SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> searchOnlyByGenre(String byGenre,
                                                                                                      int currentPage) {
        List<LatestComic> listMatchedComic = new ArrayList<>();
        String requestUrl = COMIC_BASE_URL + "loadmore?type=Theloai&cat=" + byGenre + "&p=" + currentPage;
        Document doc = this.getDocumentInstanceFromUrl(requestUrl);
        Elements comicElements = doc.select(".list-row-img");
        for (Element element : comicElements) {
            String image = element.selectFirst(".row-image a img").attr("src");
            String comicUrl = element.selectFirst(".row-info h3 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".row-info h3 a").text();
            String authorName = element.select(".row-author").text();
            String authorId = StringUtility.removeDiacriticalMarks(authorName).trim()
                    .toLowerCase().replace(" ", "-");
            Author author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            boolean isFull = false;
            var comicModel = LatestComic.builder()
                    .tagId(comicTagId)
                    .title(title)
                    .image(image)
                    .alternateImage(ALTERNATE_IMAGE)
                    .genres(genres)
                    .author(author)
                    .isFull(isFull)
                    .build();
            listMatchedComic.add(comicModel);
        }
        Pagination<Integer> pagination = new Pagination<>(currentPage, listMatchedComic.size(), 1, -1);
        PaginationUtility.updatePagination(pagination);
        return new SearchingPageableData<>(pagination, listMatchedComic, null);
    }

    @SneakyThrows
    private SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> searchOnlyByKeyword(String keyword,
                                                                                                        int currentPage) {
        List<LatestComic> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "+");
        Document doc = this.getDocumentInstanceFromUrl(COMIC_BASE_URL + "searching?key=" + term);
        Elements authorElements = doc.select(".list-author .list-content .author h3 a");
        Elements comicElements = doc.select(".list-story .list-content .list-row-img");
        List<AuthorResponse> authorList = new ArrayList<>();
        for (Element authorElement : authorElements) {
            String authorId = authorElement.attr("href").substring(authorElement.attr("href").lastIndexOf("/") + 1);
            String authorName = authorElement.text();
            authorList.add(new AuthorResponse(authorId, authorName, "Default comic tagId"));
        }
        for (Element element : comicElements) {
            String image = element.selectFirst(".row-image a img").attr("src");
            String comicUrl = element.selectFirst(".row-info h3 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".row-info h3 a").text();
            String authorName = element.select(".row-author").text();
            String authorId = StringUtility.removeDiacriticalMarks(authorName).trim()
                    .toLowerCase().replace(" ", "-");
            Author author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            boolean isFull = false;
            var comicModel = LatestComic.builder()
                    .tagId(comicTagId)
                    .title(title)
                    .image(image)
                    .alternateImage(ALTERNATE_IMAGE)
                    .genres(genres)
                    .author(author)
                    .isFull(isFull)
                    .build();
            listMatchedComic.add(comicModel);
        }
        Pagination<Integer> pagination = new Pagination<>(currentPage, listMatchedComic.size(), 1, -1);
        return new SearchingPageableData<>(pagination, listMatchedComic, authorList);
    }

    @SneakyThrows
    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        Document doc = this.getDocumentInstanceFromUrl(COMIC_BASE_URL);
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

    @SneakyThrows
    @Override
    public PageableData<Integer, List<LatestComic>> getLastedComics(int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(COMIC_BASE_URL + "loadmore?p=" + currentPage);
        List<LatestComic> lastedComics = new ArrayList<>();
        Elements elements = doc.select(".list-row-img");
        for (Element element : elements) {
            var anchorTag = element.selectFirst(".row-info a");
            String title = anchorTag.text();
            String comicUrl = anchorTag.attr("href");
            String tagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String image = element.selectFirst(".row-image a img").attr("src");
            var authorTag = element.selectFirst(".row-author");
            String authorName = authorTag.text();
            String authorId = StringUtility.removeDiacriticalMarks(authorName).toLowerCase().replace(" ", "-");
            Author author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            boolean isFull = false;
            var comicModel = LatestComic.builder()
                    .tagId(tagId)
                    .title(title)
                    .image(image)
                    .alternateImage(ALTERNATE_IMAGE)
                    .genres(genres)
                    .author(author)
                    .isFull(isFull)
                    .build();
            lastedComics.add(comicModel);
        }
        int perPage = elements.size();
        int totalPages = 25;
        int totalItems = totalPages * perPage;
        var pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        return new PageableData<>(pagination, lastedComics);
    }

    @SneakyThrows
    @Override
    public Comic getComicInfo(String comicTagId) {
        Document doc = this.getDocumentInstanceFromUrl(COMIC_BASE_URL + comicTagId);
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
        return Comic.builder()
                .tagId(comicTagId)
                .title(title)
                .image(image)
                .alternateImage(ALTERNATE_IMAGE)
                .genres(genres)
                .author(author)
                .description(description)
                .rate(rate)
                .isFull(isFull)
                .build();
    }

    @SneakyThrows
    private SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> getHotOrPromoteComics(int currentPage) {
        List<LatestComic> listMatchedComic = new ArrayList<>();
        String requestUrl = COMIC_BASE_URL + "loadmore?type=TruyenHay&cat=truyen-hay" + "&p=" + currentPage;
        Document doc = this.getDocumentInstanceFromUrl(requestUrl);
        Elements comicElements = doc.select(".list-row-img");
        for (Element element : comicElements) {
            String image = element.selectFirst(".row-image a img").attr("src");
            String comicUrl = element.selectFirst(".row-info h3 a").attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".row-info h3 a").text();
            String authorName = element.select(".row-author").text();
            String authorId = StringUtility.removeDiacriticalMarks(authorName).trim()
                    .toLowerCase().replace(" ", "-");
            Author author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            boolean isFull = false;
            var comicModel = LatestComic.builder()
                    .tagId(comicTagId)
                    .title(title)
                    .image(image)
                    .alternateImage(ALTERNATE_IMAGE)
                    .genres(genres)
                    .author(author)
                    .isFull(isFull)
                    .build();
            listMatchedComic.add(comicModel);
        }
        Pagination<Integer> pagination = new Pagination<>(currentPage, listMatchedComic.size(), 1, -1);
        PaginationUtility.updatePagination(pagination);
        return new SearchingPageableData<>(pagination, listMatchedComic, null);
    }

    @SneakyThrows
    @Override
    public PageableData<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(COMIC_BASE_URL + comicTagId + "?p=" + currentPage);
        Pagination<Integer> pagination;
        List<Chapter> chapters = new ArrayList<>();
        Elements elements = doc.select("#divtab ul li h4 a");
        for (Element element : elements) {
            String comicUrl = element.attr("href");
            String chapterNo = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.text();
            int chapterNumber = StringUtility.extractChapterNoFromString(title);
            if (title.contains(":")) {
                title = title.substring(title.indexOf(":") + 1).trim();
            }
            chapters.add(new Chapter(chapterNumber, chapterNo, title));
        }
        int perPage = elements.size();
        var lastPageTag = doc.select(".pagination ul.paging li.last a");
        var lastPageHref = lastPageTag.attr("href");
        int totalPages = Integer.parseInt(lastPageHref.substring(lastPageHref.lastIndexOf("=") + 1));
        String lastestChapterText = doc.selectFirst("#newchaps ul li:last-child").text();
        int lastestChapter = StringUtility.extractNumberFromString(lastestChapterText);
        pagination = new Pagination<>(currentPage, perPage, totalPages, lastestChapter);
        PaginationUtility.updatePagination(pagination);
        return new PageableData<>(pagination, chapters);
    }

    @Override
    @SneakyThrows
    public Comic getComicInfoOnOtherServer(AlternatedChapterRequest altChapterDto) {
        String tagId = this.getTagIdComicFromTitleAndAuthor(altChapterDto.title(),
                altChapterDto.authorName(), altChapterDto.comicTagId());
        return this.getComicInfo(tagId);
    }

    private String getTagIdComicFromTitleAndAuthor(String _title, String _authorName, String _comicTagId) {
        String keyword = _title;
        keyword = StringUtility.removeDiacriticalMarks(keyword).toLowerCase()
                .replace("[dich]", "").replace("- suu tam", "");
        if (keyword.contains("-")) {
            keyword = keyword.substring(0, keyword.lastIndexOf("-")).trim();
        }
        keyword = keyword.replace(" ", "+");
        var formattedAuthor = StringUtility.removeDiacriticalMarks(_authorName).toLowerCase().trim();
        Document doc = this.getDocumentInstanceFromUrl(COMIC_BASE_URL + "searching?key=" + keyword);
        Elements comicElements = doc.select(".list-story .list-content .list-row-img");
        String tagId = "";
        for (Element element : comicElements) {
            var comicUrlElement = element.selectFirst(".row-info h3 a");
            if (comicUrlElement == null) {
                throw new BusinessException(ExceptionType.GET_COMIC_TAGID_FAILED);
            }
            String comicUrl = comicUrlElement.attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String authorName = element.select(".row-author").text();
            String authorFormattedName = StringUtility.removeDiacriticalMarks(authorName).toLowerCase();
            String commonTag = StringUtility.findLongestCommonSubstring(comicTagId, _comicTagId);
            if (authorFormattedName.equals(formattedAuthor) &&
                    (commonTag.length() >= 0.5 * _comicTagId.length()
                            || _comicTagId.length() >= 0.5 * commonTag.length())) {
                tagId = comicTagId;
                break;
            }
        }
        if (tagId.isEmpty()) {
            throw new BusinessException(ExceptionType.GET_COMIC_TAGID_FAILED);
        }
        return tagId;
    }

    @SneakyThrows
    @Override
    public PageableData<String, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter) {
        Document doc = this.getDocumentInstanceFromUrl(COMIC_BASE_URL + comicTagId + "/" + currentChapter);
        var elementTitle = doc.selectFirst(".chapter-header ul li:nth-of-type(1) h2 a");
        if (elementTitle == null) {
            throw new BusinessException(ExceptionType.GET_CHAPTER_TITLE_FAILED);
        }
        String title = elementTitle.text();
        var elementChapterTitle = doc.selectFirst(".chapter-header ul li:nth-of-type(3) h3");
        if (elementChapterTitle == null) {
            throw new BusinessException(ExceptionType.GET_CHAPTER_TITLE_FAILED);
        }
        String chapterTitle = elementChapterTitle.text();
        int chapterNumber = StringUtility.extractChapterNoFromString(chapterTitle);
        if (chapterTitle.contains(":")) {
            chapterTitle = chapterTitle.substring(chapterTitle.indexOf(":") + 1).trim();
        }
        var elementContent = doc.selectFirst("#content");
        if (elementContent == null) {
            throw new BusinessException(ExceptionType.GET_COMIC_CHAPTER_CONTENT_FAILED);
        }
        String content = elementContent.html();
        var authorElement = doc.selectFirst(".chapter-header ul li:nth-of-type(2) h3 a");
        String authorName = authorElement.text();
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
        return new PageableData<>(pagination,
                new ComicChapterContent(title, chapterTitle, content, comicTagId, author, chapterNumber));
    }

    @SneakyThrows
    @Override
    public PageableData<String, ComicChapterContent> getComicChapterContentOnOtherServer(
            AlternatedChapterRequest altChapterDto) {
        // Tìm truyện chứa tên và cùng tác giả
        String tagId = this.getTagIdComicFromTitleAndAuthor(altChapterDto.title(),
                altChapterDto.authorName(), altChapterDto.comicTagId());
        // Tìm chapter
        String chapterUrl = "";
        int currentPage = 1;
        while (true) {
            var result = this.getChapters(tagId, currentPage);
            var chapters = result.getData();
            if (chapters == null) {
                throw new BusinessException(ExceptionType.GET_COMIC_CHAPTER_LIST_FAILED);
            }
            for (Chapter chapter : chapters) {
                if (chapter.getChapterNumber() == altChapterDto.chapterNumber()) {
                    chapterUrl = chapter.getChapterNo();
                    break;
                }
            }
            if (!chapterUrl.isEmpty() || chapters.isEmpty()) {
                break;
            }
            currentPage++;
        }
        return this.getComicChapterContent(tagId, chapterUrl);
    }

    @Override
    @SneakyThrows
    public PageableData<Integer, List<LatestComic>> getComicsByAuthor(String authorId, String tagId, int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(COMIC_BASE_URL + "tac-gia/" + authorId);
        List<LatestComic> lastedComics = new ArrayList<>();
        Elements elements = doc.select(".list-row-img");
        for (Element element : elements) {
            var anchorTag = element.selectFirst(".row-info a");
            String title = anchorTag.text();
            String comicUrl = anchorTag.attr("href");
            String comicTagId = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String image = element.selectFirst(".row-image a img").attr("src");
            var authorTag = element.selectFirst(".row-author");
            String authorName = authorTag.text();
            var author = new Author(authorId, authorName);
            List<Genre> genres = new ArrayList<>();
            boolean isFull = false;
            var comicModel = LatestComic.builder()
                    .tagId(comicTagId)
                    .title(title)
                    .image(image)
                    .alternateImage(ALTERNATE_IMAGE)
                    .genres(genres)
                    .author(author)
                    .isFull(isFull)
                    .build();
            lastedComics.add(comicModel);
        }
        int perPage = elements.size();
        int totalPages = 1;
        int totalItems = -1;
        var pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
        PaginationUtility.updatePagination(pagination);
        return new PageableData<>(pagination, lastedComics);
    }
}
