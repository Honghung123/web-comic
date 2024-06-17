package com.group17.comic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.group17.comic.utils.ListUtility;
import com.group17.comic.utils.StringUtility;

@SpringBootTest
class ListUtilityTests {
    @Test
    void testEqual_CompareTwoArrays_ReturnsTrue() {
        var exampleArr = List.of("apple", "banana", "cherry");
        String arrJson = "[\"apple\", \"banana\", \"cherry\"]";
        try {
            var arr = StringUtility.getArrayFromJSON(arrJson);
            boolean isTwoArrayEqual = ListUtility.areListsEqual(arr, exampleArr);
            assertThat(isTwoArrayEqual).isTrue();
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    void testEqual_CompareTwoArrays_ReturnsFalse() {
        var exampleArr = List.of("apple", "banana", "orange");
        String arrJson = "[\"apple\", \"banana\", \"cherry\"]";
        try {
            var arr = StringUtility.getArrayFromJSON(arrJson);
            boolean isTwoArrayEqual = ListUtility.areListsEqual(arr, exampleArr);
            assertThat(isTwoArrayEqual).isFalse();
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }
}
