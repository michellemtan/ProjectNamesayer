package model;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.resources.SetUp;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene scene = SetUp.getInstance().startMenu; // load menu scene
        SetUp.getInstance().startMenuController.setUp();
        primaryStage.setTitle("Name Sayer");
        //TODO: make work with no internet connections
        primaryStage.getIcons().add(new Image("/model/resources/images/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
