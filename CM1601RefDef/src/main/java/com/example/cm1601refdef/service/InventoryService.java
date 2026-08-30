package com.example.cm1601refdef.service;

import com.example.cm1601refdef.objects.Part;
import com.example.cm1601refdef.parsers.InventoryParser;
import com.example.cm1601refdef.utils.SortUtil;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class InventoryService {
    private ArrayList<Part> parts = new ArrayList<>();
    private final String filePath;
    private final AuditService auditService = AuditService.getDefault();
    public InventoryService(String filePath) {
        this.filePath = filePath;
        loadInventory();
    }
    public void loadInventory() {
        parts = InventoryParser.parseInventoryFile(filePath);
        SortUtil.sortPartCatCode(parts);
    }
    public void saveInventory() {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(filePath))) {
            for (Part part : parts) {
                writer.write(part.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving inventory: "
                    + e.getMessage());
        }
    }
    public boolean addPart(Part part) {
        if (getPartByCode(part.getPartCode()) != null) {
            return false;
        }
        parts.add(part);
        SortUtil.sortPartCatCode(parts);
        saveInventory();
        return true;
    }
    public boolean updatePart(String code, Part updatedPart) {
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).getPartCode().equalsIgnoreCase(code)) {
                parts.set(i, updatedPart);
                // Re-sort after modification
                SortUtil.sortPartCatCode(parts);
                saveInventory();
                return true;
            }
        }
        return false;
    }
    public boolean deletePart(String code) {
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).getPartCode().equalsIgnoreCase(code)) {
                parts.remove(i);
                saveInventory();
                return true;
            }
        }
        return false;
    }

    public void persistAndResort() {
        SortUtil.sortPartCatCode(parts);
        saveInventory();
    }

    public boolean partCodeExists(String code) {
        return getPartByCode(code) != null;
    }
    public ArrayList<Part> getAllParts() {
        return parts;
    }
    public Part getPartByCode(String code) {
        for (Part part : parts) {
            if (part.getPartCode().equalsIgnoreCase(code)) {
                return part;
            }
        }
        return null;
    }
    public ArrayList<Part> getLowStockItems(int threshold) {
        ArrayList<Part> lowStock = new ArrayList<>();
        for (Part part : parts) {
            if (part.getQuantity() < threshold) {
                lowStock.add(part);
            }
        }
        return lowStock;
    }
    public double getTotalInventoryValue() {
        double total = 0;
        for (Part part : parts) {
            total += part.getPrice() * part.getQuantity();
        }
        return total;
    }
    public int getTotalItemCount() {
        int total = 0;
        for (Part part : parts) {
            total += part.getQuantity();
        }
        return total;
    }
    public boolean reduceStock(String partCode, int quantitySold) {
        Part part = getPartByCode(partCode);
        if (part == null) {
            return false;
        }
        if (quantitySold <= 0) {
            return false;
        }
        if (quantitySold > part.getQuantity()) {
            return false;
        }
        part.setQuantity(
                part.getQuantity() - quantitySold
        );
        saveInventory();
        auditService.logSaleAsCashier("partCode=" + partCode + ", quantitySold=" + quantitySold);
        return true;
    }
}
