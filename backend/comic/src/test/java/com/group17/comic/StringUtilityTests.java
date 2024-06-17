package com.group17.comic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;

import com.group17.comic.utils.StringUtility;

@SpringBootTest
class StringUtilityTests {
    @Test
    void testRemoveDiacriticalMarks_ReturnTrue() {
        String input = "Thành phố Hồ Chí Minh";
        String expected = "Thanh pho Ho Chi Minh";
        String actual = StringUtility.removeDiacriticalMarks(input);
        assertEquals(expected, actual);
    }

    @Test
    void testRemoveDiacriticalMarks_ReturnFalse() {
        String input = "Thành phố Hồ Chí Minh";
        String notExpected = "Thành phố Hồ Chí Minh";
        String actual = StringUtility.removeDiacriticalMarks(input);
        assertNotEquals(notExpected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"abcdef, zabxy"})
    void testFindLongestCommonSubstring_ReturnSubstring(String str1, String str2) {
        String expected = "ab";
        String actual = StringUtility.findLongestCommonSubstring(str1, str2);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"abcdef, ''"})
    void testFindLongestCommonSubstring_ReturnEmptyString(String str1, String str2) {
        String expected = "";
        String actual = StringUtility.findLongestCommonSubstring(str1, str2);
        assertEquals(expected, actual);
    }

    @Test
    void testFindLongestCommonSubstring_NoCommonSubstring() {
        String str1 = "abc";
        String str2 = "xyz";
        String expected = "";
        String actual = StringUtility.findLongestCommonSubstring(str1, str2);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"abcdef, abcdef"})
    void testFindLongestCommonSubstring_ReturnSameStrings(String str1, String str2) {
        String expected = "abcdef";
        String actual = StringUtility.findLongestCommonSubstring(str1, str2);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"abcdef, zabxy"})
    void testFindLongestCommonSubstring_ReturnDifferentStrings(String str1, String str2) {
        String notExpected = "abc";
        String actual = StringUtility.findLongestCommonSubstring(str1, str2);
        assertNotEquals(notExpected, actual);
    }

    @Test
    void testExtractNumberFromStringNoLetters_ReturnNumber() {
        String input = "123456";
        int expected = 123456;
        int actual = StringUtility.extractNumberFromString(input);
        assertEquals(expected, actual);
    }

    @Test
    void testExtractNumberFromEmptyString_ThrowException() {
        String input = "";
        assertThrows(NumberFormatException.class, () -> StringUtility.extractNumberFromString(input));
    }
}
