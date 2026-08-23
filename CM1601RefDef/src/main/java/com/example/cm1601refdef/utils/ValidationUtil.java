package com.example.javacw.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class ValidationUtil {

    public static double parsePrice(String rawPrice) {
        if (rawPrice == null || rawPrice.trim().isEmpty()) {
            return 0.0;
        }
        String cleaned = rawPrice.trim();

        cleaned = cleaned.replace("Rs.", "")
                .replace("Rs", "")
                .replace(",", "")
                .trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    public static int parseQuantity(String rawQty) {
        if (rawQty == null || rawQty.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(rawQty.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    public static String safeString(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
    public static String normalizeCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return "UNKNOWN";
        }
        return category.trim().toUpperCase();
    }
    public static boolean isValidPartCode(String code) {
        return code != null && code.matches("P\\d+");
    }
    public static boolean isValidDealerCode(String code) {
        return code != null && code.matches("D\\d+");
    }

    public static boolean isNonNegativePrice(double price) {
        return price >= 0;
    }

    public static boolean isNonNegativeQuantity(int quantity) {
        return quantity >= 0;
    }

    public static String standardizeDate(String rawDate) {
        if (rawDate == null || rawDate.trim().isEmpty()) {
            return "";
        }

        String trimmed = rawDate.trim();
        DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String[] formats = {
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "MMM dd, yyyy",
                "dd-MMM-yyyy",
                "yyyy/MM/dd",
                "dd-MM-yyyy"
        };

        for (String format : formats) {
            try {
                LocalDate date = LocalDate.parse(trimmed, DateTimeFormatter.ofPattern(format, Locale.ENGLISH));
                return date.format(targetFormatter);
            } catch (DateTimeParseException e) {
            }
        }

        return trimmed;
    }
}
