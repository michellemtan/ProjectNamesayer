package model.resources;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class InvalidDBController extends AbstractController {

    @FXML private Button okBtn;
    @FXML private BorderPane bground;

    public void setUp(String name) {
        bground.getStyleClass().add("root-clean");
    }

    @FXML
    private void extBtnPressed() {
        if(stage!=null) {
            stage.close();
        }
    }
}
