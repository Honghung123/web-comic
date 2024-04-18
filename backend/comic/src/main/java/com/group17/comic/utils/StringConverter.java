package com.group17.comic.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringConverter {
    // Chuyển tiếng Việt có dấu thành không dấu:  Thành công -> Thanh cong
    public static String removeDiacriticalMarks(String str) {
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ','d').replace('Đ','D').trim();
    } 
}
