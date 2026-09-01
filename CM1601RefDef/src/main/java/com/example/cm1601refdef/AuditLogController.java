package com.example.javacw;

import com.example.javacw.objects.AuditLogEntry;
import com.example.javacw.service.AuditService;
import com.example.javacw.utils.SceneNavigationUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class AuditLogController implements Initializable {
    @FXML
    private Button inventoryDashboardButton;
    @FXML
    private Button posCheckoutButton;
    @FXML
    private Button dealerSelectionButton;
    @FXML
    private Button auditLoButton;
    @FXML
    private TableView<AuditLogEntry> auditlog;
    @FXML
    private TableColumn<AuditLogEntry, String> timestampColumn;
    @FXML
    private TableColumn<AuditLogEntry, String> roleColumn;
    @FXML
    private TableColumn<AuditLogEntry, String> actionColumn;
    @FXML
    private TableColumn<AuditLogEntry, String> entityColumn;
    @FXML
    private TableColumn<AuditLogEntry, String> detailsColumn;
    @FXML
    private Button exportAuditLogButton;

    private final AuditService auditService = AuditService.getDefault();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SceneNavigationUtil.setupNavigationButtons(
                inventoryDashboardButton,
                posCheckoutButton,
                dealerSelectionButton,
                auditLoButton
        );

        setupTableColumns();
        loadAuditLog();

        if (exportAuditLogButton != null) {
            exportAuditLogButton.setOnAction(event -> exportAuditLog());
        }
    }

    private void setupTableColumns() {
        if (timestampColumn != null) timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        if (roleColumn != null) roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        if (actionColumn != null) actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        if (entityColumn != null) entityColumn.setCellValueFactory(new PropertyValueFactory<>("entity"));
        if (detailsColumn != null) detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
    }

    private void loadAuditLog() {
        if (auditlog == null) return;
        auditlog.getItems().clear();
        auditlog.getItems().addAll(auditService.readAll());
    }

    private void exportAuditLog() {
        String exportPath = "src/main/java/com/example/javacw/data/audit_log_export.txt";
        try {
            auditService.exportAuditLog(exportPath);
            auditService.logAuditExportAsManager(exportPath);
            loadAuditLog();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export");
            alert.setContentText("Audit log exported to: " + exportPath);
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setContentText("Failed to export audit log: " + e.getMessage());
            alert.showAndWait();
        }
    }
}

