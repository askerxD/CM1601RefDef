package com.example.cm1601refdef;

import com.example.cm1601refdef.objects.CartItem;
import com.example.cm1601refdef.objects.Part;
import com.example.cm1601refdef.service.CartService;
import com.example.cm1601refdef.service.InventoryService;
import com.example.cm1601refdef.utils.SceneNavigationUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class POSCheckoutController implements Initializable {
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
    private Label subtotal;
    @FXML
    private Label TOTAL;
    @FXML
    private Label bulkDiscount;
    @FXML
    private Label synergyDiscount;
    @FXML
    private TableView<Part> inventoryTable;
    @FXML
    private TableView<CartItem> cartTable;
    @FXML
    private Button proceedToCheckout;
    @FXML
    private Button clearCart;
    @FXML
    private Button ADD;
    @FXML
    private Button REMOVE;
    @FXML
    private TextField ADDqty;
    @FXML
    private TextField REMOVEqty;

    private InventoryService inventoryService;
    private final CartService cartService = new CartService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SceneNavigationUtil.setupNavigationButtons(
                inventoryDashboardButton,
                posCheckoutButton,
                dealerSelectionButton,
                auditLoButton
        );

        inventoryService = new InventoryService(INVENTORY_PATH);
        setupInventoryTableColumns();
        setupCartTableColumns();
        loadInventoryTable();
        refreshCartTable();
        updateTotals();

        ADD.setOnAction(event -> handleAddToCart());
        REMOVE.setOnAction(event -> handleRemoveFromCart());
        clearCart.setOnAction(event -> handleClearCart());
        proceedToCheckout.setOnAction(event -> handleCheckout());
    }

    private void setupInventoryTableColumns() {
        TableColumn<Part, String> partCodeCol = (TableColumn<Part, String>) inventoryTable.getColumns().get(0);
        partCodeCol.setCellValueFactory(new PropertyValueFactory<>("partCode"));

        TableColumn<Part, String> brandCol = (TableColumn<Part, String>) inventoryTable.getColumns().get(1);
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<Part, String> descriptionCol = (TableColumn<Part, String>) inventoryTable.getColumns().get(2);
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Part, Integer> qtyCol = (TableColumn<Part, Integer>) inventoryTable.getColumns().get(3);
        qtyCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(getAvailableQuantity(cellData.getValue())).asObject());

        TableColumn<Part, Double> priceCol = (TableColumn<Part, Double>) inventoryTable.getColumns().get(4);
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void setupCartTableColumns() {
        TableColumn<CartItem, String> partCodeCol = (TableColumn<CartItem, String>) cartTable.getColumns().get(0);
        partCodeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPart().getPartCode()));

        TableColumn<CartItem, String> brandCol = (TableColumn<CartItem, String>) cartTable.getColumns().get(1);
        brandCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPart().getBrand()));

        TableColumn<CartItem, String> descriptionCol = (TableColumn<CartItem, String>) cartTable.getColumns().get(2);
        descriptionCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPart().getName()));

        TableColumn<CartItem, Number> qtyCol = (TableColumn<CartItem, Number>) cartTable.getColumns().get(3);
        qtyCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()));

        TableColumn<CartItem, Double> priceCol = (TableColumn<CartItem, Double>) cartTable.getColumns().get(4);
        priceCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getSubtotal()).asObject());
    }

    private void loadInventoryTable() {
        inventoryTable.getItems().clear();
        inventoryTable.getItems().addAll(inventoryService.getAllParts());
    }

    private void refreshCartTable() {
        cartTable.getItems().clear();
        cartTable.getItems().addAll(cartService.getCartItems());
        cartTable.refresh();
        inventoryTable.refresh();
        updateTotals();
    }

    private int getAvailableQuantity(Part part) {
        return cartService.getAvailableQuantity(part);
    }

    private void handleAddToCart() {
        Part selectedPart = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedPart == null) {
            showErrorAlert("No Selection", "Please select a part from the inventory table.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(ADDqty.getText().trim());
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Quantity", "Please enter a valid whole number for quantity.");
            return;
        }

        if (quantity <= 0) {
            showErrorAlert("Invalid Quantity", "Quantity must be greater than 0.");
            return;
        }

        if (quantity > getAvailableQuantity(selectedPart)) {
            showErrorAlert("Insufficient Stock",
                    "Quantity cannot exceed available inventory (" + getAvailableQuantity(selectedPart) + ").");
            return;
        }

        if (!cartService.addToCart(selectedPart, quantity)) {
            showErrorAlert("Cannot Add",
                    "Unable to add this quantity. Check available stock and items already in the cart.");
            return;
        }

        ADDqty.clear();
        refreshCartTable();
    }

    private void handleRemoveFromCart() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showErrorAlert("No Selection", "Please select a part from the cart table.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(REMOVEqty.getText().trim());
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Quantity", "Please enter a valid whole number for quantity.");
            return;
        }

        if (quantity <= 0) {
            showErrorAlert("Invalid Quantity", "Quantity must be greater than 0.");
            return;
        }

        if (quantity > selectedItem.getQuantity()) {
            showErrorAlert("Invalid Quantity",
                    "Quantity cannot exceed the amount in the cart (" + selectedItem.getQuantity() + ").");
            return;
        }

        cartService.removeQuantityFromCart(selectedItem.getPart().getPartCode(), quantity);
        REMOVEqty.clear();
        refreshCartTable();
    }

    private void handleClearCart() {
        if (!cartService.canCheckout()) {
            showErrorAlert("Empty Cart", "The cart is already empty.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear Cart");
        confirmAlert.setHeaderText("Clear cart?");
        confirmAlert.setContentText("Are you sure you want to remove all items from the cart?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            cartService.clearCart();
            refreshCartTable();
        }
    }

    private void handleCheckout() {
        if (!cartService.canCheckout()) {
            showErrorAlert("Empty Cart", "Please add items to the cart before checkout.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Checkout");
        confirmAlert.setHeaderText("Proceed with checkout?");
        confirmAlert.setContentText(String.format("Final total: Rs. %.2f", cartService.calculateTotal()));

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        for (CartItem item : cartService.getCartItems()) {
            String partCode = item.getPart().getPartCode();
            int quantity = item.getQuantity();
            if (!inventoryService.reduceStock(partCode, quantity)) {
                showErrorAlert("Checkout Failed",
                        "Could not complete checkout for " + partCode + ". Stock may have changed.");
                inventoryService.loadInventory();
                loadInventoryTable();
                return;
            }
        }

        cartService.clearCart();
        inventoryService.loadInventory();
        loadInventoryTable();
        refreshCartTable();

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Checkout Complete");
        successAlert.setContentText("Sale completed successfully.");
        successAlert.showAndWait();
    }

    private void updateTotals() {
        subtotal.setText(String.format("Rs. %.2f", cartService.getSubtotalBeforeDiscounts()));
        bulkDiscount.setText(String.format("- Rs. %.2f", cartService.getBulkDiscountAmount()));
        synergyDiscount.setText(String.format("- Rs. %.2f", cartService.getSynergyDiscountAmount()));
        TOTAL.setText(String.format("Rs. %.2f", cartService.calculateTotal()));
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
