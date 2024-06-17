package com.group17.comic.utils;

import java.util.List;

public class ListUtility {
    private ListUtility() {}

    public static boolean areListsEqual(List<?> list1, List<?> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        boolean result = true;
        for (int i = 0; i < list1.size(); i++) {
            boolean isEqual = false;
            for (int j = 0; j < list2.size(); j++) {
                if (list1.get(i).equals(list2.get(j))) {
                    isEqual = true;
                    break;
                }
            }
            if (!isEqual) {
                result = false;
                break;
            }
        }
        return result;
    }
}
