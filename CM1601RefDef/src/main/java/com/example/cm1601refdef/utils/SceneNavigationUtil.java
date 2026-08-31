package com.example.javacw.utils;

import com.example.javacw.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneNavigationUtil {

    public static void setupNavigationButtons(Button inventoryDashboardButton,
                                              Button posCheckoutButton,
                                              Button dealerSelectionButton,
                                              Button auditLoButton) {
        if (inventoryDashboardButton != null) {
            inventoryDashboardButton.setOnAction(event ->
                    switchToScene(inventoryDashboardButton, "dashboard.fxml", "Malambe spare depot"));
        }
        if (posCheckoutButton != null) {
            posCheckoutButton.setOnAction(event ->
                    switchToScene(posCheckoutButton, "POScheckout.fxml", "POS Checkout"));
        }
        if (dealerSelectionButton != null) {
            dealerSelectionButton.setOnAction(event ->
                    switchToScene(dealerSelectionButton, "DealerSelection.fxml", "Dealer Selection"));
        }
        if (auditLoButton != null) {
            auditLoButton.setOnAction(event ->
                    switchToScene(auditLoButton, "AuditLog.fxml", "Audit Log"));
        }
    }

    public static void switchToScene(Button sourceButton, String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFileName));
            Scene scene = new Scene(loader.load(), 1337, 725);

            Stage stage = (Stage) sourceButton.getScene().getWindow();
            stage.getIcons().clear();
            stage.getIcons().add(new Image(HelloApplication.class.getResourceAsStream("logo.png")));
            stage.setTitle(title);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to switch to " + title + " scene: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
