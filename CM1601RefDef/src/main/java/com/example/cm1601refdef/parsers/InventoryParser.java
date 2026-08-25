package com.example.cm1601refdef.parsers;

import com.example.cm1601refdef.objects.Part;
import com.example.cm1601refdef.utils.ValidationUtil;
import java.io.*;
import java.util.ArrayList;

public class InventoryParser {

    public static ArrayList<Part> parseInventoryFile(String filePath) {
        ArrayList<Part> parts = new ArrayList<>();
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = file.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] words = splitLegacyLine(line);

                String partCode = get(words, 0);
                String name = get(words, 1);
                String brand = get(words, 2);
                String priceStr = get(words, 3);
                String qtyStr = get(words, 4);
                String category = get(words, 5);
                String date = get(words, 6);
                String image = get(words, 7);

                if (!ValidationUtil.isValidPartCode(partCode)) {
                    continue;
                }

                double price = ValidationUtil.parsePrice(priceStr);
                int qty = ValidationUtil.parseQuantity(qtyStr);
                category = ValidationUtil.normalizeCategory(category);

                Part part = new Part(
                        partCode,
                        ValidationUtil.safeString(name),
                        ValidationUtil.safeString(brand),
                        price,
                        qty,
                        category,
                        ValidationUtil.standardizeDate(date),
                        ValidationUtil.safeString(image)
                );
                parts.add(part);
            }
        } catch (IOException e) {
            System.out.println("Error reading inventory file: " + e.getMessage());
        }
        return parts;
    }

    private static String[] splitLegacyLine(String line) {
        String cleanedLine = line.replace("|", ",")
                .replace(";", ",");

        String[] raw = cleanedLine.split(",", -1);
        ArrayList<String> fields = new ArrayList<>();
        for (String value : raw) {
            fields.add(value.trim());
        }


        while (fields.size() > 8) {
            String merged = fields.get(6) + ", " + fields.get(7);
            fields.set(6, merged);
            fields.remove(7);
        }

        return fields.toArray(new String[0]);
    }

    private static String get(String[] arr, int index) {
        if (index >= arr.length) {
            return "";
        }
        return arr[index].trim();
    }
}