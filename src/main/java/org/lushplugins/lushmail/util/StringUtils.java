package org.lushplugins.lushmail.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static List<String> splitByCount(String string, int count) {
        List<String> strings = new ArrayList<>();

        while (!string.isBlank()) {
            string = string.substring(0, Math.min(count, string.length()));
            strings.add(string);
        }

        return strings;
    }
}
