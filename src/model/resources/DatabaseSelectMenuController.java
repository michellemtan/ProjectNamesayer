package model.resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class DatabaseSelectMenuController {

    @FXML
    private ListView<?> dbListview;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button continueBtn;

    @FXML
    private Button backButton;

    @FXML
    void addBtnPressed(ActionEvent event) {

    }

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void continueBtnPressed(ActionEvent event) {

    }

    @FXML
    void deleteBtnPressed(MouseEvent event) {

    }

}
