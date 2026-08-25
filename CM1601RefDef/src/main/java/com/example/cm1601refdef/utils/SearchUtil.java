package com.example.cm1601refdef.utils;

import com.example.cm1601refdef.objects.Part;
import java.util.ArrayList;

public class SearchUtil {

    public static ArrayList<Part> filterParts(ArrayList<Part> parts, String keyword, String category, String minPrice, String maxPrice) {
        ArrayList<Part> filteredParts = new ArrayList<>();

        double minPriceValue = parsePrice(minPrice);
        double maxPriceValue = parsePrice(maxPrice);
        String keywordLower = keyword != null ? keyword.toLowerCase().trim() : "";
        String categoryUpper = category != null && !category.isEmpty() ? category.toUpperCase() : null;

        for (Part part : parts) {
            if (!keywordLower.isEmpty()) {
                if (!part.getPartCode().toLowerCase().contains(keywordLower) &&
                        !part.getName().toLowerCase().contains(keywordLower) &&
                        !part.getBrand().toLowerCase().contains(keywordLower)) {
                    continue;
                }
            }

            if (categoryUpper != null
                    && !part.getCategory().toUpperCase().equals(categoryUpper)) {
                continue;
            }

            if (part.getPrice() < minPriceValue || part.getPrice() > maxPriceValue) {
                continue;
            }

            filteredParts.add(part);
        }

        return filteredParts;
    }

    private static double parsePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return priceStr == null || priceStr.trim().isEmpty() ? 0.0 : Double.MAX_VALUE;
        }
        try {
            return Double.parseDouble(priceStr.trim());
        } catch (NumberFormatException e) {
            return priceStr.isEmpty() ? 0.0 : Double.MAX_VALUE;
        }
    }
}
