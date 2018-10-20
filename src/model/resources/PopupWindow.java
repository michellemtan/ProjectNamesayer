package model.resources;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class PopupWindow {

    private AbstractController popupController;

    public PopupWindow(String fxml, boolean setUpRequired, String name){
        //Load the popup window and set the controller
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        popupController = loader.getController();

        if (setUpRequired){
            popupController.setUp(name);
        }

        //Set the style of the popup window controller and give access to the popup stage (to allow the controller to close the stage)
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.UNDECORATED);
        popupController.setStage(popupStage);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    public AbstractController getController(){
        return popupController;
    }
}
