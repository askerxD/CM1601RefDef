package com.example.cm1601refdef.parsers;

import com.example.cm1601refdef.objects.Part;
import com.example.cm1601refdef.utils.ValidationUtil;
import java.io.*;
import java.util.ArrayList;

public class InventoryParser {
    private static final String DATE_COMMA_MARKER = "##DATECOMMA##";

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
        String protectedLine = protectCommaDates(line);
        protectedLine = protectedLine.replace("|", ",")
                .replace(";", ",");

        String[] raw = protectedLine.split(",", -1);
        ArrayList<String> fields = new ArrayList<>();
        for (int i = 0; i < raw.length; i++) {
            fields.add(raw[i].replace(DATE_COMMA_MARKER, ",").trim());
        }

        while (fields.size() > 8) {
            String left = fields.get(6);
            String right = fields.get(7);
            if (isFourDigitYear(right)) {
                fields.set(6, left + ", " + right);
                fields.remove(7);
            } else {
                break;
            }
        }

        return fields.toArray(new String[0]);
    }

    private static String protectCommaDates(String line) {
        String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        String result = line;
        for (int m = 0; m < months.length; m++) {
            result = protectOneMonthDate(result, months[m]);
        }
        return result;
    }

    private static String protectOneMonthDate(String line, String month) {
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < line.length()) {
            int found = indexOfIgnoreCase(line, month, i);
            if (found < 0) {
                out.append(line.substring(i));
                break;
            }
            out.append(line.substring(i, found));

            int pos = found + month.length();
            if (pos >= line.length() || line.charAt(pos) != ' ') {
                out.append(line.substring(found, found + month.length()));
                i = found + month.length();
                continue;
            }
            pos++;

            int dayStart = pos;
            while (pos < line.length() && Character.isDigit(line.charAt(pos))) {
                pos++;
            }
            if (pos == dayStart || pos >= line.length() || line.charAt(pos) != ',') {
                out.append(line.substring(found, found + month.length()));
                i = found + month.length();
                continue;
            }
            pos++;

            while (pos < line.length() && line.charAt(pos) == ' ') {
                pos++;
            }
            int yearStart = pos;
            while (pos < line.length() && Character.isDigit(line.charAt(pos))) {
                pos++;
            }
            if (pos - yearStart != 4) {
                out.append(line.substring(found, found + month.length()));
                i = found + month.length();
                continue;
            }

            String dateText = line.substring(found, pos);
            out.append(dateText.replace(",", DATE_COMMA_MARKER));
            i = pos;
        }
        return out.toString();
    }

    private static int indexOfIgnoreCase(String text, String target, int fromIndex) {
        String lowerText = text.toLowerCase();
        String lowerTarget = target.toLowerCase();
        return lowerText.indexOf(lowerTarget, fromIndex);
    }

    private static boolean isFourDigitYear(String value) {
        if (value == null || value.length() != 4) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static String get(String[] arr, int index) {
        if (index >= arr.length) {
            return "";
        }
        return arr[index].trim();
    }
}
