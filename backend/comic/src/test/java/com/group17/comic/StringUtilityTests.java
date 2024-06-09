package com.group17.comic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.group17.comic.utils.StringUtility;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StringUtilityTests {
    @Test
    public void testRemoveDiacriticalMarks_ReturnTrue() {
        String input = "Thành phố Hồ Chí Minh";
        String expected = "Thanh pho Ho Chi Minh";
        String actual = StringUtility.removeDiacriticalMarks(input);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveDiacriticalMarks_ReturnFalse() {
        String input = "Thành phố Hồ Chí Minh";
        String notExpected = "Thành phố Hồ Chí Minh";
        String actual = StringUtility.removeDiacriticalMarks(input);
        assertNotEquals(notExpected, actual);
    }

    @Test
    public void testFindLongestCommonSubstring_ReturnSubstring() {
        String str1 = "abcdef";
        String str2 = "zabxy";
        String expected = "ab";
        String actual = StringUtility.findLongestCommonSubstring(str1, str2);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindLongestCommonSubstring_ReturnEmptyString() {
        String str1 = "";
        String str2 = "abcdef";
        String expected = "";
        String actual = StringUtility.findLongestCommonSubstring(str1, str2);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindLongestCommonSubstring_NoCommonSubstring() {
        String str1 = "abc";
        String str2 = "xyz";
        String expected = "";
        String actual = StringUtility.findLongestCommonSubstring(str1, str2);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindLongestCommonSubstring_ReturnSameStrings() {
        String str1 = "abcdef";
        String str2 = "abcdef";
        String expected = "abcdef";
        String actual = StringUtility.findLongestCommonSubstring(str1, str2);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindLongestCommonSubstring_ReturnDifferentStrings() {
        String str1 = "abcdef";
        String str2 = "zabxy";
        String notExpected = "abc";
        String actual = StringUtility.findLongestCommonSubstring(str1, str2);
        assertNotEquals(notExpected, actual);
    }

    @Test
    public void testExtractNumberFromStringNoLetters_ReturnNumber() {
        String input = "123456";
        int expected = 123456;
        int actual = StringUtility.extractNumberFromString(input);
        assertEquals(expected, actual);
    }

    @Test
    public void testExtractNumberFromEmptyString_ThrowException() {
        String input = "";
        assertThrows(NumberFormatException.class, () -> StringUtility.extractNumberFromString(input));
    }

}
