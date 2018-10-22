package model.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ExitPracticeMenuController {

    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML BorderPane borderPane;
    private String previousScene = "";

    @FXML
    void cancelButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void confirmButtonClicked() throws IOException {

        Scene scene = SetUp.getInstance().enterNamesMenu;
        Stage window = (Stage) confirmButton.getScene().getWindow();
        window.setScene(scene);

        deleteFromFolder("recorded_names");
    }

    public void setPreviousScene(String name) {
        previousScene = name;
    }

    @FXML
    private void initialize(){
        borderPane.getStyleClass().add("root-clear");
    }


    private void deleteFromFolder(String pathname){
        File dir = new File(pathname);
        for(File file: dir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
}
