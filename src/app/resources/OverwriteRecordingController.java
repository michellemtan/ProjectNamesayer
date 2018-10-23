package app.resources;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;


public class OverwriteRecordingController extends AbstractController {

    @FXML private BorderPane bground;

    private boolean isOverwritten;

    //Run on cancel button being pressed
    @FXML
    void cancelButtonClicked() {
        isOverwritten = false;
        if(stage!=null) {
            stage.close();
        }
    }

    //Run on yes button being pressed
    @FXML
    void yesButtonClicked() {
        isOverwritten = true;
        if(stage!=null) {
            stage.close();
        }
    }

    //Run on switching to this scene
    public void setUp(String name) {
        bground.getStyleClass().add("root-clean");
    }

    //Helper method to return boolean result
    @Override
    public boolean getResult(){
        return isOverwritten;
    }
}
