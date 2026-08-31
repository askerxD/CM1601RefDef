package com.example.cm1601refdef;

import com.example.cm1601refdef.objects.Part;
import com.example.cm1601refdef.service.AuditService;
import com.example.cm1601refdef.service.InventoryService;
import com.example.cm1601refdef.service.PartService; // Import PartService
import com.example.cm1601refdef.utils.SceneNavigationUtil;
import com.example.cm1601refdef.utils.SearchUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class HelloController implements Initializable {
     private static final String INVENTORY_PATH = "src/main/java/com/example/javacw/data/inventory_legacy.txt";

     @FXML
     private Button inventoryDashboardButton;
     @FXML
     private Button posCheckoutButton;
     @FXML
     private Button dealerSelectionButton;
     @FXML
     private Button auditLoButton;
     @FXML
     private Button addNewPartButton;
     @FXML
     private Button updateSelectedButton;
     @FXML
     private Button deleteSelectedButton;
     @FXML
     private TextField keyworkSearchField;
     @FXML
     private TextField minPriceField;
     @FXML
     private TextField maxPriceField;
     @FXML
     private TableView<Part> inventoryTable;
     @FXML
     private Label totalnventoryValue;
     @FXML
     private Label partsCount;
     @FXML
     private VBox lowStockWarning;
     @FXML
     private ChoiceBox<String> catogoryFilterChoiceBox;
     @FXML
     private Button search;
     @FXML
     private Button resetSearch;

     private InventoryService inventoryService;
     // Removed private int currentThreshold = 4;
     private final AuditService auditService = AuditService.getDefault();

     @Override
     public void initialize(URL url, ResourceBundle resourceBundle) {
          loadInventoryData();
          // Removed setupLowStockThresholdButton();
          setupSearchButtons();
          setupAddNewPartButton();
          setupUpdateSelectedButton();
          setupDeleteSelectedButton();
          setupNavigationButtons();
          populateCategories();
     }

     private void loadInventoryData() {
          inventoryService = new InventoryService(INVENTORY_PATH);
          setupTableColumns();
          refreshTableFromService();
          // Removed lowStockThreshold.setText(String.valueOf(currentThreshold));
     }

     private void refreshTableFromService() {
          ArrayList<Part> parts = inventoryService.getAllParts();
          inventoryTable.getItems().clear();
          inventoryTable.getItems().addAll(parts);
          updateInventoryStats(parts);
          displayLowStockWarnings(); // Call without threshold
     }

     private void populateCategories() {
          Set<String> uniqueCategories = new HashSet<>();
          for (Part part : inventoryService.getAllParts()) {
               uniqueCategories.add(part.getCategory());
          }

          catogoryFilterChoiceBox.getItems().clear();
          catogoryFilterChoiceBox.getItems().add("All Categories");
          catogoryFilterChoiceBox.getItems().addAll(uniqueCategories);
          catogoryFilterChoiceBox.setValue("All Categories");
     }

     private void setupSearchButtons() {
          search.setOnAction(event -> performSearch());
          resetSearch.setOnAction(event -> resetFilters());
     }

     private void setupNavigationButtons() {
          SceneNavigationUtil.setupNavigationButtons(
                  inventoryDashboardButton,
                  posCheckoutButton,
                  dealerSelectionButton,
                  auditLoButton
          );
     }

     private void setupAddNewPartButton() {
          addNewPartButton.setOnAction(event -> openAddItemWindow());
     }

     private void setupUpdateSelectedButton() {
          if (updateSelectedButton != null) {
               updateSelectedButton.setOnAction(event -> openUpdateItemWindow());
          }
     }

     private void setupDeleteSelectedButton() {
          if (deleteSelectedButton != null) {
               deleteSelectedButton.setOnAction(event -> handleDeleteSelected());
          }
     }

     private void handleDeleteSelected() {
          Part selectedPart = inventoryTable.getSelectionModel().getSelectedItem();

          if (selectedPart == null) {
               showErrorAlert("No Selection", "Please select a part to delete.");
               return;
          }

          Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
          confirmAlert.setTitle("Confirm Delete");
          confirmAlert.setHeaderText("Delete Part?");
          confirmAlert.setContentText("Are you sure you want to delete '" + selectedPart.getPartCode()
                  + " - " + selectedPart.getName() + "'? This action cannot be undone.");

          if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
               String partCode = selectedPart.getPartCode();
               if (inventoryService.deletePart(partCode)) {
                    auditService.logInventoryDeleteAsAdmin(partCode);
                    refreshInventoryTable(); // Call refreshInventoryTable instead of resetFilters and populateCategories
                    showSuccessAlert("Success", "Part deleted successfully!");
               } else {
                    showErrorAlert("Delete Failed", "Could not delete the selected part.");
               }
          }
     }

     private void showSuccessAlert(String title, String message) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle(title);
          alert.setContentText(message);
          alert.showAndWait();
     }

     private void openAddItemWindow() {
          try {
               FXMLLoader loader = new FXMLLoader(getClass().getResource("addItemScene.fxml"));
               // Corrected dimensions for addItemScene.fxml
               Scene scene = new Scene(loader.load(), 748, 350);

               ANPController controller = loader.getController();
               PartService partService = new PartService(inventoryService); // Create PartService
               controller.setPartService(partService); // Pass PartService

               Stage stage = new Stage();
               stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
               stage.setTitle("Add New Part");
               stage.setScene(scene);
               stage.showAndWait(); // Use showAndWait to ensure the table is refreshed after the window is closed
               refreshInventoryTable(); // Refresh table after adding a part
          } catch (IOException e) {
               e.printStackTrace();
               showErrorAlert("Error", "Failed to open Add Item window");
          }
     }

     private void openUpdateItemWindow() {
          Part selectedPart = inventoryTable.getSelectionModel().getSelectedItem();

          if (selectedPart == null) {
               showErrorAlert("No Selection", "Please select a part to update.");
               return;
          }

          try {
               FXMLLoader loader = new FXMLLoader(getClass().getResource("updateItemScene.fxml"));
               // Corrected dimensions for updateItemScene.fxml
               Scene scene = new Scene(loader.load(), 838, 350);

               UPDController controller = loader.getController();
               PartService partService = new PartService(inventoryService); // Create PartService
               controller.setPartService(partService); // Pass PartService
               controller.setPart(selectedPart);

               Stage stage = new Stage();
               stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
               stage.setTitle("Update Part");
               stage.setScene(scene);
               stage.showAndWait(); // Use showAndWait to ensure the table is refreshed after the window is closed
               refreshInventoryTable(); // Refresh table after updating a part
          } catch (IOException e) {
               e.printStackTrace();
               showErrorAlert("Error", "Failed to open Update Item window: " + e.getMessage());
          } catch (Exception e) {
               e.printStackTrace();
               showErrorAlert("Error", "An unexpected error occurred: " + e.getMessage());
          }
     }

     private void showErrorAlert(String title, String message) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle(title);
          alert.setContentText(message);
          alert.showAndWait();
     }

     private void performSearch() {
          String keyword = keyworkSearchField.getText();
          String category = catogoryFilterChoiceBox.getValue();
          String minPrice = minPriceField.getText();
          String maxPrice = maxPriceField.getText();

          if (category != null && category.equals("All Categories")) {
               category = "";
          }

          if (minPrice == null || minPrice.trim().isEmpty()) {
               minPrice = "0";
          }
          if (maxPrice == null || maxPrice.trim().isEmpty()) {
               maxPrice = "999999";
          }

          ArrayList<Part> filteredParts = SearchUtil.filterParts(
                  inventoryService.getAllParts(), keyword, category, minPrice, maxPrice);

          inventoryTable.getItems().clear();
          inventoryTable.getItems().addAll(filteredParts);
          updateInventoryStats(filteredParts);
     }

     private void resetFilters() {
          keyworkSearchField.clear();
          catogoryFilterChoiceBox.setValue("All Categories");
          minPriceField.clear();
          maxPriceField.clear();
          refreshTableFromService();
     }

     private void updateInventoryStats(ArrayList<Part> parts) {
          partsCount.setText(String.valueOf(parts.size()));

          double totalValue = 0.0;
          for (Part part : parts) {
               totalValue += part.getPrice() * part.getQuantity();
          }
          totalnventoryValue.setText(String.format("Rs. %.2f", totalValue));
     }

     // Removed setupLowStockThresholdButton()

     private void displayLowStockWarnings() { // Removed threshold parameter
          lowStockWarning.getChildren().clear();

          ArrayList<Part> allParts = inventoryService.getAllParts(); // Get all parts

          for (Part part : allParts) {
               // Check against individual lowStockThreshold
               if (part.getQuantity() < part.getLowStockThreshold()) {
                    Label warningLabel = new Label();
                    String text = part.getPartCode() + " - " + part.getName() + "\n" +
                            "remaining | " + part.getQuantity() + " (Threshold: " + part.getLowStockThreshold() + ")";
                    warningLabel.setText(text);
                    warningLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14;");
                    warningLabel.setWrapText(true);
                    lowStockWarning.getChildren().add(warningLabel);
               }
          }
     }

     private void setupTableColumns() {
          TableColumn<Part, String> partCodeCol = (TableColumn<Part, String>) inventoryTable.getColumns().get(0);
          partCodeCol.setCellValueFactory(new PropertyValueFactory<>("partCode"));

          TableColumn<Part, String> descriptionCol = (TableColumn<Part, String>) inventoryTable.getColumns().get(1);
          descriptionCol.setCellValueFactory(new PropertyValueFactory<>("name"));

          TableColumn<Part, String> brandCol = (TableColumn<Part, String>) inventoryTable.getColumns().get(2);
          brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));

          TableColumn<Part, Double> priceCol = (TableColumn<Part, Double>) inventoryTable.getColumns().get(3);
          priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

          TableColumn<Part, Integer> qtyCol = (TableColumn<Part, Integer>) inventoryTable.getColumns().get(4);
          qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

          TableColumn<Part, String> categoryCol = (TableColumn<Part, String>) inventoryTable.getColumns().get(5);
          categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

          TableColumn<Part, String> lastUpdatedCol = (TableColumn<Part, String>) inventoryTable.getColumns().get(6);
          lastUpdatedCol.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));

          TableColumn<Part, String> imageCol = (TableColumn<Part, String>) inventoryTable.getColumns().get(7);
          imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
          imageCol.setCellFactory(new Callback<TableColumn<Part, String>, TableCell<Part, String>>() {
               @Override
               public TableCell<Part, String> call(TableColumn<Part, String> param) {
                    return new TableCell<Part, String>() {
                         @Override
                         protected void updateItem(String item, boolean empty) {
                              super.updateItem(item, empty);
                              if (empty || item == null || item.isEmpty()) {
                                   setGraphic(null);
                                   setText(null);
                              } else {
                                   try {
                                        Image image = new Image(getClass().getResourceAsStream("/com/example/javacw/" + item));
                                        ImageView imageView = new ImageView(image);
                                        imageView.setFitHeight(50);
                                        imageView.setFitWidth(50);
                                        setGraphic(imageView);
                                        setText(null);
                                   } catch (Exception e) {
                                        System.err.println("Error loading image: " + item + " - " + e.getMessage());
                                        setGraphic(null);
                                        setText("Image Not Found");
                                   }
                              }
                         }
                    };
               }
          });


          TableColumn<Part, Integer> thresholdCol = (TableColumn<Part, Integer>) inventoryTable.getColumns().get(8);
          thresholdCol.setCellValueFactory(new PropertyValueFactory<>("lowStockThreshold"));
     }



     public void refreshInventoryTable() {
          inventoryService.persistAndResort();
          populateCategories();
          resetFilters();
          displayLowStockWarnings();
     }
}