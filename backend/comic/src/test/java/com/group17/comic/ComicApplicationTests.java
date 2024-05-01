package com.group17.comic;  

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import com.group17.comic.plugins.exporter.concretes.PdfConverter;
import com.group17.comic.utils.StringUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

@SpringBootTest
class ComicApplicationTests {

	@Test
	void createPDF() throws IOException {
		var text = """
			<h1>Hello World!</h1><a href='https://pdf.co'>Go to PDF.co</a>
		""";
//		PdfConverter.createPdfFromText(text, "test.pdf");
	}

	@Test
	void testPath () {
	  String keyword = "";
        String byGenre = "";
	  int result = 0;
        if (StringUtils.hasLength(keyword) && StringUtils.hasLength(byGenre)) {
            result = 1;
        } else if (keyword.isEmpty() && StringUtils.hasLength(byGenre)) {
            result = 2;
        } else if (StringUtils.hasLength(keyword) && byGenre.isEmpty()) {
            result = 3;
        } else {
            System.out.println("None of them has value");
        }
	  assertEquals(0, result);
	}
}
