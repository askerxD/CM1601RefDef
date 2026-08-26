package com.example.cm1601refdef.objects;

import javafx.beans.property.SimpleStringProperty;

public class AuditLogEntry {
    private final SimpleStringProperty timestamp;
    private final SimpleStringProperty role;
    private final SimpleStringProperty action;
    private final SimpleStringProperty entity;
    private final SimpleStringProperty details;

    public AuditLogEntry(String timestamp, String role, String action, String entity, String details) {
        this.timestamp = new SimpleStringProperty(timestamp);
        this.role = new SimpleStringProperty(role);
        this.action = new SimpleStringProperty(action);
        this.entity = new SimpleStringProperty(entity);
        this.details = new SimpleStringProperty(details);
    }

    public String getTimestamp() {
        return timestamp.get();
    }

    public String getRole() {
        return role.get();
    }

    public String getAction() {
        return action.get();
    }

    public String getEntity() {
        return entity.get();
    }

    public String getDetails() {
        return details.get();
    }
}
