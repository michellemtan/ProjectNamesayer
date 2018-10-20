package model;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.resources.PopupWindow;
import model.resources.SetUp;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene scene = SetUp.getInstance().startMenu; //Load menu scene
        primaryStage.setTitle("Name Sayer");
        primaryStage.getIcons().add(new Image("/model/resources/images/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        //Check for presence of default database
        File f = new File(System.getProperty("user.dir") + "/names");
        if(!f.exists() || !f.isDirectory()) {
            //Create stage and scene for small alert regarding missing database
            SetUp.getInstance().startMenuController.buttonsOff(true);
            SetUp.getInstance().settingsMenuController.disableBack(true);
            PopupWindow p = new PopupWindow("model/views/MissingDB.fxml", false, null);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
