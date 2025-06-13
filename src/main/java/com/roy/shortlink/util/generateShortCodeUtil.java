package com.roy.shortlink.util;
import java.util.UUID;
public class generateShortCodeUtil{
    private static final String CHAR_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateShort(String originUrl){

        int hash = Math.abs(originUrl.hashCode() ^ UUID.randomUUID().toString().hashCode());
        StringBuilder shortCode = new StringBuilder();
        while (hash > 0 && shortCode.length() < 8) {
            shortCode.append(CHAR_SET.charAt(hash % 62));
            hash /= 62;
        }
        return shortCode.toString();
    }
}

