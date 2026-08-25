package com.example.cm1601refdef.parsers;

import com.example.cm1601refdef.objects.Dealer;
import com.example.cm1601refdef.utils.ValidationUtil;
import java.io.*;
import java.util.ArrayList;

public class DealerParser {
    public static ArrayList<Dealer> parseDealerFile(String filePath) {

        ArrayList<Dealer> dealers = new ArrayList<>();

        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = file.readLine()) != null) {

                if (line.trim().isEmpty()){
                    continue;
                }
                line = line.replace("|", ",")
                        .replace(";", ",");

                String[] words = line.split(",");

                String dealerId = get(words, 0);
                String name = get(words, 1);
                String contact = get(words, 2);
                String location = get(words, 3);

                if (!ValidationUtil.isValidDealerCode(dealerId)) {
                    continue;
                }
                Dealer dealer = new Dealer(
                        dealerId.trim(),
                        ValidationUtil.safeString(name),
                        ValidationUtil.safeString(contact),
                        ValidationUtil.safeString(location)
                );
                dealers.add(dealer);
            }
        } catch (IOException e) {
            System.out.println("Error reading dealer file: " + e.getMessage());
        }
        return dealers;
    }
    private static String get(String[] arr, int index) {
        if (index >= arr.length) {
            return "";
        }else{
            return arr[index].trim();
        }
    }
}
