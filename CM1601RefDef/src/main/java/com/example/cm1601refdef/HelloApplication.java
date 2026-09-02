package com.example.cm1601refdef;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1337, 725);
        stage.setTitle("Malambe spare depot");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}