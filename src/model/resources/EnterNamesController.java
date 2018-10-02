package model.views;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.resources.SetUp;

import java.io.IOException;

public class EnterNamesController {

    @FXML
    private Text backButton;

    @FXML
    private ListView<?> databaseNamesListView;

    @FXML
    private ListView<?> practiceNamesListView;

    @FXML
    void addButtonClicked(MouseEvent event) {

    }

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().databaseSelectMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void practiceButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void searchButtonClicked(MouseEvent event) {

    }

}
