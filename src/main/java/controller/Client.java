package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        Client.primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        stage.setTitle("Hello World");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}