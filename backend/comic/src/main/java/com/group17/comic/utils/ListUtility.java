package com.group17.comic.utils;

import java.util.List;

public class ListUtility {
    public static boolean areListsEqual(List<?> list1, List<?> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }
}
