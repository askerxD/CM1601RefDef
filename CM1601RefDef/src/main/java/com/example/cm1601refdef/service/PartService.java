package com.example.cm1601refdef.service;

import com.example.cm1601refdef.objects.Part;
import com.example.cm1601refdef.utils.ValidationUtil;

import java.time.LocalDate;

public class PartService {

    private InventoryService inventoryService;

    public PartService(InventoryService inventoryService) { // Constructor accepts InventoryService
        this.inventoryService = inventoryService;
    }

    public boolean partCodeExists(String partCode) {
        return inventoryService.partCodeExists(partCode);
    }

    public void addPart(Part part) throws IllegalArgumentException {
        if (partCodeExists(part.getPartCode())) {
            throw new IllegalArgumentException("Part code '" + part.getPartCode() + "' already exists.");
        }
        if (!inventoryService.addPart(part)) {
            throw new IllegalArgumentException("Failed to add part to inventory.");
        }
    }

    public void validatePartData(String partCode, String description, String brand, double price, int stockQty, String category, LocalDate dateAdded, String imageName, int lowStockThresholdValue) throws IllegalArgumentException {
        if (partCode.isEmpty()) {
            throw new IllegalArgumentException("Part Code is required.");
        }
        if (!ValidationUtil.isValidPartCode(partCode)) {
            throw new IllegalArgumentException("Part Code must match format P followed by digits (e.g. P011).");
        }
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description is required.");
        }
        if (brand.isEmpty()) {
            throw new IllegalArgumentException("Brand is required.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (stockQty < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("Please select a category.");
        }
        if (dateAdded == null) {
            throw new IllegalArgumentException("Last Updated date is required.");
        }
        if (lowStockThresholdValue < 0) {
            throw new IllegalArgumentException("Low Stock Threshold cannot be negative.");
        }
    }
}
