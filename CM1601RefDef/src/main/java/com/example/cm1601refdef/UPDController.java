package com.example.javacw;

import com.example.javacw.objects.Part;
import com.example.javacw.service.AuditService;
import com.example.javacw.service.PartService;
import com.example.javacw.utils.ValidationUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UPDController implements Initializable {
    @FXML
    private TextField PartCode;
    @FXML
    private TextField Description;
    @FXML
    private TextField Brand;
    @FXML
    private TextField Price;
    @FXML
    private TextField StockQty;
    @FXML
    private ChoiceBox<String> Category;
    @FXML
    private DatePicker LastUpdated;
    @FXML
    private Button SavePart;
    @FXML
    private Button cancel;
    @FXML
    private Button updateImage;
    @FXML
    private TextField lsThreshold;
    @FXML
    private ImageView partImage;
    @FXML
    private TextField image;


    private PartService partService;
    private Part selectedPart;
    private String originalPartCode;
    private final AuditService auditService = AuditService.getDefault();

    public void setPartService(PartService partService) { // Update setter
        this.partService = partService;
    }

    public void setPart(Part part) {
        this.selectedPart = part;
        this.originalPartCode = part != null ? part.getPartCode() : null;
        populateFields(part);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupCategoryDropdown();
        setupButtonHandlers();
        image.textProperty().addListener((observable, oldValue, newValue) -> updateImageView(newValue));
    }

    private void setupCategoryDropdown() {
        Category.getItems().addAll("ENGINE", "ELECTRICAL", "BRAKES", "BODYWORK");
    }

    private void setupButtonHandlers() {
        cancel.setOnAction(event -> handleCancel());
        SavePart.setOnAction(event -> handleSavePart());
    }

    private void populateFields(Part part) {
        if (part != null) {
            PartCode.setText(part.getPartCode());
            Description.setText(part.getName());
            Brand.setText(part.getBrand());
            Price.setText(String.valueOf(part.getPrice()));
            StockQty.setText(String.valueOf(part.getQuantity()));
            Category.setValue(ValidationUtil.normalizeCategory(part.getCategory()));
            lsThreshold.setText(String.valueOf(part.getLowStockThreshold()));

            image.setText(part.getImage());
            updateImageView(part.getImage());

            try {
                String dateStr = part.getDateAdded();
                LocalDate date;

                try {
                    date = LocalDate.parse(dateStr);
                } catch (Exception e1) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        date = LocalDate.parse(dateStr, formatter);
                    } catch (Exception e2) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                        date = LocalDate.parse(dateStr, formatter);
                    }
                }
                LastUpdated.setValue(date);
            } catch (Exception e) {
                System.out.println("Error parsing date: " + part.getDateAdded());
                LastUpdated.setValue(LocalDate.now());
            }
        }
    }

    private void handleCancel() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancel");
        confirmAlert.setHeaderText("Cancel Operation?");
        confirmAlert.setContentText("Are you sure you want to cancel? Any unsaved changes will be lost.");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            closeWindow();
        }
    }

    private void handleSavePart() {
        try {
            String partCode = PartCode.getText().trim().toUpperCase();
            String description = Description.getText().trim();
            String brand = Brand.getText().trim();
            double price = Double.parseDouble(Price.getText().trim());
            int stockQty = Integer.parseInt(StockQty.getText().trim());
            String category = Category.getValue();
            LocalDate dateAdded = LastUpdated.getValue();
            String imageName = image.getText().trim();
            int lowStockThresholdValue = Integer.parseInt(lsThreshold.getText().trim());

            partService.validatePartData(partCode, description, brand, price, stockQty, category, dateAdded, imageName, lowStockThresholdValue);

            if (!partCode.equalsIgnoreCase(originalPartCode) && partService.partCodeExists(partCode)) {
                showErrorAlert("Duplicate Part Code",
                        "Part code '" + partCode + "' already exists. Please use a unique code.");
                return;
            }

            if (selectedPart != null) {
                String before = "name=" + safe(selectedPart.getName()) +
                        ", brand=" + safe(selectedPart.getBrand()) +
                        ", price=" + selectedPart.getPrice() +
                        ", qty=" + selectedPart.getQuantity() +
                        ", cat=" + safe(selectedPart.getCategory()) +
                        ", date=" + safe(selectedPart.getDateAdded()) +
                        ", threshold=" + selectedPart.getLowStockThreshold() +
                        ", image=" + safe(selectedPart.getImage());

                selectedPart.setPartCode(partCode);
                selectedPart.setName(description);
                selectedPart.setBrand(brand);
                selectedPart.setPrice(price);
                selectedPart.setQuantity(stockQty);
                selectedPart.setCategory(ValidationUtil.normalizeCategory(category));
                selectedPart.setDateAdded(dateAdded != null
                        ? dateAdded.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                selectedPart.setLowStockThreshold(lowStockThresholdValue);
                selectedPart.setImage(imageName);

                String after = "name=" + safe(description) +
                        ", brand=" + safe(brand) +
                        ", price=" + price +
                        ", qty=" + stockQty +
                        ", cat=" + safe(ValidationUtil.normalizeCategory(category)) +
                        ", date=" + safe(selectedPart.getDateAdded()) +
                        ", threshold=" + lowStockThresholdValue +
                        ", image=" + safe(selectedPart.getImage());

                auditService.logInventoryUpdateAsAdmin(partCode, before + " -> " + after);
            }

            showSuccessAlert("Success", "Part updated successfully!");
            closeWindow();
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Input", "Price, Stock Qty, and Low Stock Threshold must be valid numbers.");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Validation Error", e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateImageView(String imageName) {
        if (imageName != null && !imageName.isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream("/com/example/javacw/" + imageName));
                partImage.setImage(img);
                partImage.setFitHeight(100);
                partImage.setFitWidth(100);
            } catch (Exception e) {
                System.err.println("Error loading image in UPDController: " + imageName + " - " + e.getMessage());
                partImage.setImage(null);
            }
        } else {
            partImage.setImage(null);
        }
    }

    private String safe(String value) {
        if (value == null) return "";
        return value.trim();
    }
}