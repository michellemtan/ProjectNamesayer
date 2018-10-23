package app.resources;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class InvalidDBController extends AbstractController {

    @FXML private BorderPane bground;

    //Helper method to assign custom CSS class
    public void setUp(String name) {
        bground.getStyleClass().add("root-clean");
    }

    //Run on exit button being pressed
    @FXML
    private void extBtnPressed() {
        if(stage!=null) {
            stage.close();
        }
    }
}
