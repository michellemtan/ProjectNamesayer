package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import app.resources.PopupWindow;
import app.resources.SetUp;

import java.io.File;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = SetUp.getInstance().startMenu; //Load menu scene
        primaryStage.setTitle("Name Sayer");
        primaryStage.getIcons().add(new Image("/app/resources/images/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        //Check for presence of default database
        File f = new File(System.getProperty("user.dir") + "/names");
        if(!f.exists() || !f.isDirectory()) {
            //Create stage and scene for small alert regarding missing database
            SetUp.getInstance().startMenuController.buttonsOff(true);
            SetUp.getInstance().settingsMenuController.disableBack(true);
            PopupWindow p = new PopupWindow("app/views/MissingDB.fxml", false, null);
        }else if(f.listFiles() != null && Objects.requireNonNull(f.listFiles()).length >0 && Objects.requireNonNull(f.listFiles())[0].getName().endsWith(".wav")) {
            SetUp.getInstance().settingsMenuController.setNeedToProcessDefault(true);
        }

        //If main stage is closed, close all other pop ups
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
