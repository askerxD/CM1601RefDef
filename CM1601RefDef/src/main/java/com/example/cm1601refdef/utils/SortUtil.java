package com.example.cm1601refdef.utils;

import com.example.cm1601refdef.objects.Dealer;
import com.example.cm1601refdef.objects.Part;
import java.util.ArrayList;

public class SortUtil {
    public static void sortDealersByLocation(ArrayList<Dealer> dealers) {

        for (int i = 0; i < dealers.size() - 1; i++) {

            for (int j = 0; j < dealers.size() - i - 1; j++) {

                String current =
                        dealers.get(j).getLocation().toLowerCase();

                String next =
                        dealers.get(j + 1).getLocation().toLowerCase();

                if (current.compareTo(next) > 0) {

                    Dealer temp = dealers.get(j);
                    dealers.set(j, dealers.get(j + 1));
                    dealers.set(j + 1, temp);
                }
            }
        }
    }

    public static void sortPartCatCode(ArrayList<Part> parts) {
        for (int i = 0; i < parts.size() - 1; i++) {

            for (int j = 0; j < parts.size() - i - 1; j++) {
                Part current = parts.get(j);
                Part next = parts.get(j + 1);
                String currentCategory =
                        current.getCategory().toUpperCase();

                String nextCategory =
                        next.getCategory().toUpperCase();
                boolean swap = false;
                if (currentCategory.compareTo(nextCategory) > 0) {
                    swap = true;
                }
                else if (currentCategory.equals(nextCategory)) {

                    if (current.getPartCode()
                            .compareTo(next.getPartCode()) > 0) {

                        swap = true;
                    }
                }
                if (swap) {
                    Part temp = parts.get(j);
                    parts.set(j, parts.get(j + 1));
                    parts.set(j + 1, temp);
                }
            }
        }
    }
}
