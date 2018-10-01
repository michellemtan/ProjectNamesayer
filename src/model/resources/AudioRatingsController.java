package model.resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AudioRatingsController {

    @FXML
    private Button backButton;

    @FXML
    private Button clearButton;

    @FXML
    private TextArea textArea;

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException {
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void clearTextLog(MouseEvent event) {

    }

}
