package com.example.history_service.util;

public class PhoneNumberUtil {

    public static String mask(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return phone;
        }
        String cleaned = phone.trim();
        int len = cleaned.length();
        if (len <= 4) {
            return cleaned.charAt(0) + "**" + cleaned.charAt(len - 1);
        }
        String prefix = cleaned.substring(0, 2);
        String suffix = cleaned.substring(len - 2);
        int middleCount = len - 4;
        String stars = "*".repeat(Math.max(3, middleCount));
        return prefix + stars + suffix;
    }
}
