package com.example.javacw;

import com.example.javacw.objects.Part;
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

public class ANPController implements Initializable {
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
    private TextField lsThreshold;
    @FXML
    private TextField image;
    @FXML
    private Button addImage;
    @FXML
    private ImageView partImage;


    private PartService partService;

    public void setPartService(PartService partService) {
        this.partService = partService;
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

    private void handleCancel() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancel");
        confirmAlert.setHeaderText("Cancel Operation?");
        confirmAlert.setContentText("Are you sure you want to cancel? Any unsaved data will be lost.");

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

            int lowStockThresholdValue = 5;
            if (!lsThreshold.getText().trim().isEmpty()) {
                lowStockThresholdValue = Integer.parseInt(lsThreshold.getText().trim());
            }

            partService.validatePartData(partCode, description, brand, price, stockQty, category, dateAdded, imageName, lowStockThresholdValue);

            String normalizedCategory = ValidationUtil.normalizeCategory(category);
            String dateAddedStr = dateAdded != null
                    ? dateAdded.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            Part newPart = new Part(partCode, description, brand, price, stockQty, normalizedCategory, dateAddedStr, imageName, lowStockThresholdValue);

            partService.addPart(newPart);

            showSuccessAlert("Success", "Part added successfully!");
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
                System.err.println("Error loading image in ANPController: " + imageName + " - " + e.getMessage());
                partImage.setImage(null);
            }
        } else {
            partImage.setImage(null);
        }
    }
}