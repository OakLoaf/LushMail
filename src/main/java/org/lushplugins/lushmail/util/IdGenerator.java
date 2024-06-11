package org.lushplugins.lushmail.util;

import org.lushplugins.lushmail.LushMail;

import java.util.Random;

public class IdGenerator {
    private static final char[] ID_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String generateRandomAlphanumeric(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = LushMail.getRandom();

        while (stringBuilder.length() < length) {
            int randIndex = random.nextInt(ID_CHARS.length);
            stringBuilder.append(ID_CHARS[randIndex]);
        }

        return stringBuilder.toString();
    }
}
