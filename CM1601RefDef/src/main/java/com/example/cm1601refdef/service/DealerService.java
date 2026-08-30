package com.example.cm1601refdef.service;

import com.example.cm1601refdef.objects.Dealer;
import com.example.cm1601refdef.parsers.DealerParser;
import com.example.cm1601refdef.utils.SortUtil;
import java.util.ArrayList;
import java.util.Random;

public class DealerService {
    private ArrayList<Dealer> dealers = new ArrayList<>();
    private final String filePath;
    public DealerService(String filePath) {
        this.filePath = filePath;
        loadDealers();
    }
    public void loadDealers() {
        dealers = DealerParser.parseDealerFile(filePath);
    }
    public ArrayList<Dealer> getAllDealers() {
        return dealers;
    }
    public ArrayList<Dealer> getRandomFourDealers() {

        ArrayList<Dealer> selected = new ArrayList<>();
        Random random = new Random();

        if (dealers.size() <= 4) {
            selected = new ArrayList<>(dealers);
            SortUtil.sortDealersByLocation(selected);
            return selected;
        }
        while (selected.size() < 4) {

            int index = random.nextInt(dealers.size());

            Dealer dealer = dealers.get(index);

            if (!containsDealer(selected, dealer.getDealerId())) {
                selected.add(dealer);
            }
        }
        SortUtil.sortDealersByLocation(selected);
        return selected;
    }
    private boolean containsDealer(ArrayList<Dealer> list, String dealerId) {
        for (Dealer dealer : list) {
            if (dealer.getDealerId().equals(dealerId)) {
                return true;
            }
        }
        return false;
    }
}
