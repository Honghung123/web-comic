package com.group17.comic;  

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.group17.comic.plugins.exporter.concretes.PdfConverter;
import com.group17.comic.utils.StringUtility;

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
		String a = "dichdau-pha-thuong-khungfull";
		String b = 	"truyen-dau-pha-thuong-khung";
		String c = StringUtility.findLongestCommonSubstring(a, b);
		System.out.println(c);
	}
}
