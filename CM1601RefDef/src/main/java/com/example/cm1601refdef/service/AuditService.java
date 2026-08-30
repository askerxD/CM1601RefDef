package com.example.cm1601refdef.service;

import com.example.cm1601refdef.objects.AuditLogEntry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AuditService {
    public static final String DEFAULT_AUDIT_LOG_PATH = "src/main/java/com/example/javacw/data/audit_log.txt";

    private final String filePath;
    public AuditService(String filePath) {
        this.filePath = filePath;
    }

    public static AuditService getDefault() {
        return new AuditService(DEFAULT_AUDIT_LOG_PATH);
    }

    public void logInventoryAddAsAdmin(String partCode, int quantity) {
        writeLog("ADMIN", "ADD", partCode, "quantity=" + quantity);
    }

    public void logInventoryDeleteAsAdmin(String partCode) {
        writeLog("ADMIN", "DELETE", partCode, "");
    }

    public void logInventoryUpdateAsAdmin(String partCode, String details) {
        writeLog("ADMIN", "UPDATE", partCode, details == null ? "" : details);
    }

    public void logSaleAsCashier(String details) {
        writeLog("CASHIER", "SALE", "POS", details == null ? "" : details);
    }

    public void logAuditExportAsManager(String exportPath) {
        writeLog("MANAGER", "EXPORT", "AUDIT_LOG", exportPath == null ? "" : exportPath);
    }

    public void logLowStockThresholdChangeAsManager(int oldThreshold, int newThreshold) {
        writeLog("MANAGER", "LOW_STOCK_THRESHOLD_CHANGE", "INVENTORY",
                "old=" + oldThreshold + ", new=" + newThreshold);
    }

    public void exportAuditLog(String exportPath) throws IOException {
        ensureParentExists(exportPath);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(exportPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public ArrayList<AuditLogEntry> readAll() {
        ArrayList<AuditLogEntry> entries = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return entries;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                AuditLogEntry parsed = parseLine(line);
                if (parsed != null) {
                    entries.add(parsed);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading audit log: " + e.getMessage());
        }
        return entries;
    }

    private AuditLogEntry parseLine(String line) {
        if (line == null) {
            return null;
        }
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String[] parts = trimmed.split("\\s*\\|\\s*", -1);

        if (parts.length >= 5) {
            return new AuditLogEntry(parts[0], parts[1], parts[2], parts[3], joinFrom(parts, 4));
        }

        if (parts.length == 4) {
            String timestamp = parts[0];
            String action = parts[1];
            String entity = parts[2];
            String details = "quantity=" + parts[3];
            return new AuditLogEntry(timestamp, "UNKNOWN", action, entity, details);
        }

        return null;
    }

    private String joinFrom(String[] parts, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) {
            if (i > startIndex) sb.append(" | ");
            sb.append(parts[i]);
        }
        return sb.toString();
    }

    private void ensureParentExists(String path) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("Failed to create directory: " + parent.getAbsolutePath());
            }
        }
    }

    private void writeLog(String role, String action, String entity, String details) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String safeDetails = safe(details);
        if (safeDetails.isEmpty()) {
            safeDetails = "-";
        }

        String logEntry = timestamp +
                " | " + safe(role) +
                " | " + safe(action) +
                " | " + safe(entity) +
                " | " + safeDetails;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(logEntry);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing audit log: " + e.getMessage());
        }
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\n", " ").replace("\r", " ").trim();
    }
}
