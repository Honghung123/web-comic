package com.group17.comic;  

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import com.group17.comic.plugins.exporter.concretes.PdfConverter;
import com.group17.comic.utils.StringUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.regex.Pattern;

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
	  String a = "Chương 07 Nghịch thiên tà 7 thần";
	  int expected = 7;
      //   String b = "Chương"; 
	//   assertEquals(false, a.contains(b));
	//   if(a.contains(b)){
	// 	a = a.replace(b, "").trim();
	// 	a = a.substring(a.indexOf(" ") + 1);
	//   }
	//   assertEquals(false, a.contains(b));
	String regex = "\\d+";
	Pattern pattern = Pattern.compile(regex);
	var mat = pattern.matcher(a);
	if(mat.find()){
		assertEquals(expected, Integer.parseInt(mat.group(0)));
	}
	}
}
