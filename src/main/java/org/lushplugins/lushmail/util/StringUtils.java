package org.lushplugins.lushmail.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    /**
     * Splits a string into multiple strings of count + 10
     * @param string string to split
     * @param count amount to split by
     * @return List of split strings
     */
    public static List<String> splitByCount(String string, int count) {
        List<String> strings = new ArrayList<>();

        String copy = string;
        while (copy.length() > count) {
            int index = Math.min(copy.lastIndexOf(" ", count), count + 10);
            strings.add(copy.substring(0, index).strip());
            copy = copy.substring(index);
        }

        if (!copy.isBlank()) {
            strings.add(copy.strip());
        }

        return strings;
    }

    public static String shortenString(String string, int count) {
        if (string.length() < count) {
            return string;
        }

        int index = Math.min(string.lastIndexOf(" ", count), count + 10);
        if (index == -1) {
            return string;
        }

        return string.substring(0, index) + "...";
    }
}
