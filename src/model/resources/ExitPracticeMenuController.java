package model.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ExitPracticeMenuController {

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    @FXML
    void cancelButtonClicked() throws IOException {

        //Change scenes
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void confirmButtonClicked() throws IOException {
        //Reset the practice menu
        SetUp.getInstance().practiceMenuController.clearListView();

        //Change scenes
        Scene scene = SetUp.getInstance().databaseMenu;
        Stage window = (Stage) confirmButton.getScene().getWindow();
        window.setScene(scene);
    }
}
