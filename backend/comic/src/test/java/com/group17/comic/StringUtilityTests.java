package com.group17.comic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.group17.comic.utils.ListUtility;
import com.group17.comic.utils.StringUtility;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StringUtilityTests {
    @Test
    public void getArrayFromJSONTest(){
        var exampleArr = List.of("apple", "banana", "cherry" );
        String arrJson = "[\"apple\", \"banana\", \"cherry\"]";
        try {
            var arr = StringUtility.getArrayFromJSON(arrJson);
            boolean isTwoArrayEqual = ListUtility.areListsEqual(arr, exampleArr);
            assertThat(isTwoArrayEqual).isTrue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
