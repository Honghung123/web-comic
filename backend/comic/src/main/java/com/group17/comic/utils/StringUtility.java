package com.group17.comic.utils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group17.comic.enums.ExceptionType;
import com.group17.comic.exceptions.BusinessException;

public class StringUtility {
    private StringUtility() {}
    // Chuyển tiếng Việt có dấu thành không dấu:  Thành công -> Thanh cong
    public static String removeDiacriticalMarks(String str) {
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp)
                .replaceAll("")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .trim();
    }

    public static String findLongestCommonSubstring(String str1, String str2) {
        if (str1.isEmpty() || str2.isEmpty()) {
            return "";
        }
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        int maxLength = 0;
        int endIndexStr1 = 0;
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    if (dp[i][j] > maxLength) {
                        maxLength = dp[i][j];
                        endIndexStr1 = i;
                    }
                } else {
                    dp[i][j] = 0;
                }
            }
        }
        return str1.substring(endIndexStr1 - maxLength, endIndexStr1);
    }

    public static int extractNumberFromString(String str) {
        String numberStr = str.replaceAll("\\D+", "");
        return Integer.parseInt(numberStr);
    }

    public static int extractChapterNoFromString(String str) throws NumberFormatException {
        String regex = "\\d+";
        var matcher = Pattern.compile(regex).matcher(str);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(0));
        } else {
            throw new NumberFormatException("Can't get chapter number");
        }
    }

    public static List<String> getArrayFromJSON(String json) {
        var objMapper = new ObjectMapper();
        List<String> strList = null;
        try {
            strList = Arrays.asList(objMapper.readValue(json, String[].class));
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.INVALID_PLUGIN_ID_LIST);
        }
        return strList;
    }

    public static String removeHtmlTags(String content) {
        var defaultHtmlTags =
                List.of("h1", "h2", "h3", "h4", "h5", "h6", "div", "p", "br", "span", "hr", "ul", "li", "ol");
        return removeWithSpecificHtmlTags(content, defaultHtmlTags);
    }

    public static String removeWithSpecificHtmlTags(String html, List<String> tags) {
        for (String tag : tags) {
            String regexOpen = "<" + tag + "(\\s+[^>]*)?>";
            String regexClose = "</" + tag + ">";
            html = html.replaceAll(regexOpen, "");
            html = html.replaceAll(regexClose, "");
            html = html.replaceAll("<\\s*(hr|br)\\s*/>", "");
        }
        return html;
    }
}
