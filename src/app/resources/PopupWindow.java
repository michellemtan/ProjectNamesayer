package app.resources;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class PopupWindow {

    private AbstractController popupController;
    private Stage popupStage;

    //Constructor
    public PopupWindow(String fxml, boolean setUpRequired, String name) throws IOException {
        //Load the popup window and set the controller
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene.getStylesheets().add(SetUp.getInstance().settingsMenuController.getTheme());

        popupController = loader.getController();

        //Invoke setUp method in controller of pop-up if invoked as such
        if (setUpRequired){
            popupController.setUp(name);
        }

        //Set the style of the popup window controller and give access to the popup stage (to allow the controller to close the stage)
        popupStage = new Stage();
        popupStage.initStyle(StageStyle.UNDECORATED);
        popupController.setStage(popupStage);
        popupStage.setAlwaysOnTop(true);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    //Helper method to return controller
    public AbstractController getController(){
        return popupController;
    }
}
