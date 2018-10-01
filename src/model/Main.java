package model;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.resources.SetUp;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene scene = SetUp.getInstance().startMenu; // load menu scene
        primaryStage.setTitle("Name Sayer");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
