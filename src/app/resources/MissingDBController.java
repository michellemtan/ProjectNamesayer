package app.resources;

import javafx.fxml.FXML;

public class MissingDBController extends AbstractController {

    //Run on ok button being pressed
    @FXML
    public void okButtonClicked(){
        stage.close();
    }

}
