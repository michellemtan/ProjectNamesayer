package model.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ExitPracticeMenuController {

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    private String previousScene = "";

    @FXML
    void cancelButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void confirmButtonClicked() throws IOException {

        //Delete all saved recordings and created names from the session
       deleteFromFolder("created_names");
       deleteFromFolder("recorded_names");

        if (previousScene.equals("enterNamesMenu")) {
            Scene scene = SetUp.getInstance().enterNamesMenu;
            Stage window = (Stage) confirmButton.getScene().getWindow();
            window.setScene(scene);
        }
    }

    public void setPreviousScene(String name) {
        previousScene = name;
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
