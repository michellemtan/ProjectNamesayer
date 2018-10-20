package model.resources;

import javafx.stage.Stage;

public abstract class AbstractController {

    Stage stage = null;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean getResult(){
        return false;
    }

    public void setUp(String name){
    }
}
