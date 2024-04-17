package com.group17.comic;

import static org.junit.jupiter.api.Assertions.assertTrue;

// import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ComicApplicationTests {

	@Test
	void contextLoads() {
		var totalItemsText = "Danh sach chuong(123 chuong)";
		int totalItems = 0;
        var totalItemMatcher = Pattern.compile("\\w*\\((\\d+)\\w*\\)\\w*").matcher(totalItemsText);
        if (totalItemMatcher.find()) {
            totalItems = Integer.parseInt(totalItemMatcher.group(1));
        }
		assertTrue(totalItems == 123); 
	}

}
