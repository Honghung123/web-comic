package com.group17.comic.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtility {
    // Chuyển tiếng Việt có dấu thành không dấu:  Thành công -> Thanh cong
    public static String removeDiacriticalMarks(String str) {
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ','d').replace('Đ','D').trim();
    }
    
    public static String findLongestCommonSubstring(String str1, String str2) {
        if (str1.length() == 0 || str2.length() == 0) {
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
}
