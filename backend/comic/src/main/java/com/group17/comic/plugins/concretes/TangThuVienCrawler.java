package com.group17.comic.plugins.concretes;
 
import java.io.IOException; 
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

public class TangThuVienCrawler implements WebCrawler, IDocument {
    private final String TRUYEN_URL = "https://truyen.tangthuvien.vn/";  

    @Override
    public DataModel<List<ComicModel>> search(String keyword, int currentPage) {
        List<ComicModel> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "%20");
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "ket-qua-tim-kiem?term=" + term); 
        Elements elements = doc.select("#rank-view-list .book-img-text ul li");
        for (Element element : elements) {
            String image = element.selectFirst(".book-img-box a img").attr("src");
            String comicUrl = element.selectFirst(".book-mid-info h4 a").attr("href"); 
            String url = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".book-mid-info h4 a").text();
            var authorTag = element.select(".book-mid-info .author a:nth-of-type(1)");
            String author = authorTag.text();
            String authorUrl = authorTag.attr("href");
            String authorId = authorUrl.substring(authorUrl.lastIndexOf("/") + 1);
            List<Genre> genres = new ArrayList<>();
            var genreTag = element.select(".book-mid-info .author a:nth-of-type(2)"); 
            String fullTag = genreTag.attr("href").substring(genreTag.attr("href").lastIndexOf("the-loai"));
            String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
            String label = genreTag.text();
            genres.add(new Genre(label, tag, fullTag));
            var chapterTag = element.select(".book-mid-info .author span span.KIBoOgno");
            int totalChapter = Integer.parseInt(chapterTag.text()); 
            String updatedTime = element.selectFirst(".book-mid-info .update span").text();
            listMatchedComic.add(new ComicModel(url, title, image, WebCrawler.alternateImage, genres, totalChapter, totalChapter, updatedTime));
        }
        String pattern = """
            <ul class="pagination">
            <li class="disabled"><span>«</span></li> 
            <li class="active"><span>1</span></li>
            <li><a href="https://truyen.tangthuvien.vn/ket-qua-tim-kiem?page=2">2</a></li><li><a href="https://truyen.tangthuvien.vn/ket-qua-tim-kiem?page=3">3</a></li><li><a href="https://truyen.tangthuvien.vn/ket-qua-tim-kiem?page=4">4</a></li>
            <li><a href="https://truyen.tangthuvien.vn/ket-qua-tim-kiem?page=5">5</a></li>
            <li><a href="https://truyen.tangthuvien.vn/ket-qua-tim-kiem?page=6">6</a></li>
            <li><a href="https://truyen.tangthuvien.vn/ket-qua-tim-kiem?page=7">7</a></li>
            <li><a href="https://truyen.tangthuvien.vn/ket-qua-tim-kiem?page=8">8</a></li>
            <li class="disabled"><span>...</span></li>
            <li><a href="https://truyen.tangthuvien.vn/ket-qua-tim-kiem?page=26">26</a></li>
            <li><a href="https://truyen.tangthuvien.vn/ket-qua-tim-kiem?page=27">27</a></li> 
            <li><a href="https://truyen.tangthuvien.vn/ket-qua-tim-kiem?page=2" rel="next">»</a></li>
            </ul>
            """;
            var perPage = elements.size();
            var lastAnchorTag = doc.select("ul.pagination li:nth-last-child(2) a");
            int totalPages = 1;
            int totalItems = perPage;
            if(lastAnchorTag.size() == 1) {
               totalPages = Integer.parseInt(lastAnchorTag.text()); 
               totalItems = totalPages * perPage;
            }
        var pagination = new Pagination(currentPage, perPage, totalPages, totalItems);
        DataModel<List<ComicModel>> dataDto = new DataModel<>(pagination, listMatchedComic);
        return dataDto;
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<Genre>(); 
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL); 
        Elements elements = doc.select("div#classify-list dd a");
        for (Element element : elements) {
            String url = element.attr("href");
            if(url.contains("the-loai")) {
                String fullTag = url.substring(url.lastIndexOf("the-loai"));
                String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
                String label = element.select("span.info i").text();
                genres.add(new Genre(label, tag, fullTag));
            }
        }
        return genres;
    }

    @Override
    public DataModel<List<ComicModel>> getLastedComics(int currentPage) throws IOException {  
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "tong-hop?tp=cv&page=" + currentPage);
        String pattern = """
            <div class="rank-view-list" id="rank-view-list">
            <div class="book-img-text">
                <ul>
                                                                                <li>
                                <div class="book-img-box">
                                    <a href="https://truyen.tangthuvien.vn/doc-truyen/ta-la-toi-truy-tinh-1" target="_blank">
                                        <img class="lazy" src="https://www.nae.vn/ttv/ttv/public/images/story/d4fafe01ba6268bcefa9fdce91fa8817ea12a19f45ecb42e0115b90b375207bc.jpg" onerror="this.src='https://truyen.tangthuvien.vn/images/default-book.png'" alt="Ta Là Tới Truy Tinh (Ngã Thị Lai Truy Tinh)">
                                    </a>
                                </div>
                                <div class="book-mid-info">
                                    <h4><a href="https://truyen.tangthuvien.vn/doc-truyen/ta-la-toi-truy-tinh-1" target="_blank">Ta Là Tới Truy Tinh (Ngã Thị Lai Truy Tinh)</a></h4>
                                    <p class="author">
                                        <img src="https://truyen.tangthuvien.vn/images/app/user.f22d3.png">
                                        <a class="name" href="https://truyen.tangthuvien.vn/tac-gia?author=24987" target="_blank" data-eid="qd_C41">40 Cảnh Cáo</a>
                                        <em>|</em>
                                        <a href="https://truyen.tangthuvien.vn/the-loai/do-thi" target="_blank" data-eid="qd_C42">Đô Thị</a>
                                        <em>|</em>
                                        <span>Đang ra</span><em>|</em><span><span class="KIBoOgno">573</span>&nbsp;chương</span>
                                    </p>
                                    <p class="intro">
                                        Đạo sư: “Từ trên tư liệu nhìn, ngươi gia cảnh phi thường tốt, nhân sinh của ngươi có rất nhiều lựa chọn, ngươi vì sao lại tới tham gia cái tiết mục này? 

Có...
                                    </p>
                                    <p class="update">Cập nhật: <span>2024-04-13 22:30:29</span>
                                    </p>
                                </div>
                                <div class="book-right-info">
                                    <div class="total">
                                    </div>
                                    <p class="btn">
                                        <a class="red-btn" href="https://truyen.tangthuvien.vn/doc-truyen/ta-la-toi-truy-tinh-1?read_now=1" target="_blank" data-eid="qd_C37" data-bid="1010868264">Đọc truyện</a>
                                        <a class="blue-btn add-book" href="https://truyen.tangthuvien.vn/doc-truyen/ta-la-toi-truy-tinh-1" data-eid="qd_C38" data-bookid="1010868264" data-bid="1010868264">Chi tiết</a>
                                    </p>
                                </div>
                            </li>                                                         
                                                                    </ul>
            </div>
        </div>
                """;;
                List<ComicModel> lastedComics = new ArrayList<>();
        Elements elements = doc.select("div#rank-view-list ul li");
        for (Element element : elements) {
            String image = element.selectFirst(".book-img-box a img").attr("src");
            String comicUrl = element.selectFirst(".book-mid-info h4 a").attr("href"); 
            String url = comicUrl.substring(comicUrl.lastIndexOf("/") + 1);
            String title = element.selectFirst(".book-mid-info h4 a").text();
            var authorTag = element.select(".book-mid-info .author a:nth-of-type(1)");
            String author = authorTag.text();
            String authorUrl = authorTag.attr("href");
            String authorId = authorUrl.substring(authorUrl.lastIndexOf("/") + 1);
            List<Genre> genres = new ArrayList<>();
            var genreTag = element.select(".book-mid-info .author a:nth-of-type(2)"); 
            String fullTag = genreTag.attr("href").substring(genreTag.attr("href").lastIndexOf("the-loai"));
            String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
            String label = genreTag.text();
            genres.add(new Genre(label, tag, fullTag));
            var chapterTag = element.select(".book-mid-info .author span span.KIBoOgno");
            int totalChapter = Integer.parseInt(chapterTag.text()); 
            String updatedTime = element.selectFirst(".book-mid-info .update span").text();
            lastedComics.add(new ComicModel(url, title, image, WebCrawler.alternateImage, genres, totalChapter, totalChapter, updatedTime));
        }
        int perPage = elements.size();
        var lastAnchorTag = doc.select("ul.pagination li:nth-last-of-type(2) a");
        int totalPages = 1;
        int totalItems = perPage;
        if(lastAnchorTag.size() == 1) {
            totalPages = Integer.parseInt(lastAnchorTag.text());
            totalItems = totalPages * perPage;
        }
        var pagination = new Pagination(currentPage, perPage, totalPages, totalItems);
        DataModel<List<ComicModel>> result = new DataModel<> (pagination, lastedComics);
        return result;
    }

    @Override
    public Comic getComicInfo(String comicTagId) {  
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/" + comicTagId);
        String comit= """
            <div class="book-information cf">
            <div class="book-img">
                <a class="J-getJumpUrl" id="bookImg" href="javascript:void(0);">
                    <img src="https://www.nae.vn/ttv/ttv/public/images/story/9516b42e2a63489a08647babacd70b30e1914fd6339ece93699e597cb0f3d56c.jpg" onerror="this.src='https://truyen.tangthuvien.vn/images/default-book.png'">
                </a>
            </div>
            <div class="book-info ">
                <h1>Thôn Phệ Tinh Không 2 Khởi Nguyên Đại Lục  - 吞噬星空 2 起源大陆 </h1>
                <p class="tag">
                    <a href="https://truyen.tangthuvien.vn/tac-gia?author=100" class="blue">Ngã Cật Tây Hồng Thị</a>           
                     <span class="blue">Đang ra</span>                                            
                    <a href="https://truyen.tangthuvien.vn/the-loai/khoa-huyen" class="red" target="_blank" data-eid="qd_G10">Khoa Huyễn</a>
                        
                </p>
                <p class="intro">Tương lai thế giới      
            Tinh hải dạo chơi, xuyên qua không gian và...</p>
                <p>
                                <em><span class="ULtwOOTH-like">11 </span></em><cite>Yêu thích</cite><i>|</i>
                    <em><span class="ULtwOOTH-view">9459</span></em><cite>Lượt xem</cite><i>|</i>
                    <em><span class="ULtwOOTH-follow">50 </span></em><cite>Theo dõi</cite><i>|</i>
                    <em><span class="ULtwOOTH-nomi">55</span></em><cite>Đề cử tháng này</cite>
                </p><p>
                    <a class="red-btn J-getJumpUrl" href="https://truyen.tangthuvien.vn/doc-truyen/thon-phe-tinh-khong-2-khoi-nguyen-dai-luc/chuong-1 " id="readBtn">Đọc truyện</a>
                    <a class="blue-btn add-book " id="addLikeBtn" onclick="likeStory('37937');" href="javascript:">Yêu thích</a>
                    <a class="blue-btn add-book " id="addFollowBtn" onclick="followStory('37937');" href="javascript:">Theo dõi</a>
                    <a class="blue-btn" id="topVoteBtn" href="javascript:" onclick="nominateStory('37937')" data-vote="0">Đề cử</a>
                </p>
            </div>
            <div class="comment-wrap">
                <div id="commentWrap">
                    <div class="j_getData">
                        <h4 id="j_bookScore"><span><cite id="myrate">5</cite></span></h4>
                        <span style="display: none">5</span>
                        <p id="j_userCount"><span id="myrating">5</span>đánh giá</p>
                    </div>
                    <h5>Ta muốn đánh giá</h5>
                    <div class="score-mid" id="scoreBtn" data-score="0" data-comment="0" data-eid="qd_G12" style="cursor: pointer; width: 116px;" onclick="openReview();">
                                                            <img src=" https://truyen.tangthuvien.vn/images/star-on.02731.png " alt="1" title="Quá tệ">
                                            <img src=" https://truyen.tangthuvien.vn/images/star-on.02731.png " alt="2" title="Không hay lắm">
                                            <img src=" https://truyen.tangthuvien.vn/images/star-on.02731.png " alt="3" title="Cũng được">
                                            <img src=" https://truyen.tangthuvien.vn/images/star-on.02731.png " alt="4" title="Khá hay">
                                            <img src=" https://truyen.tangthuvien.vn/images/star-on.02731.png " alt="5" title="Rất hay">
                                    </div>
                </div>
            </div>
            
        </div>
                """;
        Element element = doc.selectFirst("div.book-information");
        String image = element.select(".book-img img").attr("src");
        String title = element.selectFirst(".book-info h1").text();
        var authorTag = element.select(".book-info .tag a:nth-of-type(1)");
        int authorId = Integer.parseInt(authorTag.attr("href").substring(authorTag.attr("href").lastIndexOf("=") + 1));
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
        if(description.contains("Bước 1")){
            description = description.substring(0, description.indexOf("Bước 1") - 1);
        }
        return new Comic(comicTagId, title, image, WebCrawler.alternateImage, description, author, genres, true);
    }

    @Override
    public DataModel<List<Chapter>> getChapters(String comicTagId, int currentPage) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/" + comicTagId);
        Pagination pagination = null;
        List<Chapter> chapters = new ArrayList<>();
        var totalItemsText = doc.getElementById("j-bookCatalogPage").text();
        int totalItems = 0;
        var totalItemMatcher = Pattern.compile("\\((\\d+) chương\\)").matcher(totalItemsText);
        if (totalItemMatcher.find()) {
            totalItems = Integer.parseInt(totalItemMatcher.group(1)); 
        }else {
            Logger.logError(this.getClass().getSimpleName() + " Can't get total items in pagination", new Exception("Can't get total items in pagination"));
        }
        int comicId = Integer.parseInt(doc.getElementById("story_id_hidden").val()); 
        Document chaperListDoc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/page/" + comicId + "?page=" + (currentPage-1));
        Elements elements = chaperListDoc.select(".col-md-6 ul li a span");
        int perPage = elements.size();
        for (Element element : elements) { 
            int chapterNo = 0;
            String title = "";
            var chapterMatcher = Pattern.compile("Chương\\s*(\\d+)\\s*:\\s*(.*)").matcher(element.text());
            if (chapterMatcher.find()) {
                chapterNo = Integer.parseInt(chapterMatcher.group(1));
                title = chapterMatcher.group(2);
                chapters.add(new Chapter(chapterNo, title));
            }else {
                Logger.logError(this.getClass().getSimpleName() + " Can't get chapter number and title", new Exception("Can't get chapter number"));
            }
        }        var paginationTag = chaperListDoc.select("ul.pagination li:last-child a");
        String totalPageText = paginationTag.attr("onclick");
        int totalPages = 0;
        Matcher totalPageMatcher = Pattern.compile("\\((\\d+)\\)").matcher(totalPageText);
        if (totalPageMatcher.find()) {
            totalPages = Integer.parseInt(totalPageMatcher.group(1)) + 1; 
        }else {
            Logger.logError(this.getClass().getSimpleName() + " Can't get total items in pagination", new Exception("Can't get total items in pagination"));
        }         
        pagination = new Pagination(currentPage, perPage, totalPages, totalItems);
        DataModel<List<Chapter>> result = new DataModel<>(pagination, chapters);
        return result;
    } 

    @Override
    public DataModel<ComicChapterContent> getComicChapterContent(String comicTagId, int currentChapter) {
        Document doc = this.getDocumentInstanceFromUrl(TRUYEN_URL + "doc-truyen/" + comicTagId + "/chuong-" + currentChapter);
        var elementTitle = doc.selectFirst(".chapter-c-content h5 a");
        if(elementTitle == null) {
            Logger.logError(this.getClass().getSimpleName() + " Can't get chapter content", new Exception("Can't get chapter content"));
            throw new ResourceNotFound(HttpStatus.NOT_FOUND, "Can't get chapter content");
        } 
        String title = elementTitle.text().substring(elementTitle.text().lastIndexOf(":")+1).trim();
        var elementContent = doc.selectFirst(".chapter-c-content .box-chap");
        if(elementContent == null) {
            Logger.logError(this.getClass().getSimpleName() + " Can't get chapter content", new Exception("Can't get chapter content"));
            throw new ResourceNotFound(HttpStatus.NOT_FOUND, "Can't get chapter content");
        }
        String content = elementContent.html(); 
        Pagination paginationTemp = getChapters(comicTagId, 1).getPagination();
        Pagination pagination = new Pagination(currentChapter, 1, paginationTemp.getTotalItems(), paginationTemp.getTotalItems());
        DataModel<ComicChapterContent> result = new DataModel<>(pagination, new ComicChapterContent(title, content, comicTagId));
        return result;
    }
}
