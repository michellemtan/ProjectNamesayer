package model.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class StartMenuController {

    @FXML private Button startButton;
    @FXML private Button instructionsButton;
    @FXML private Button micButton;
    @FXML private Button sadFaceButton;

/*    public void initialize() {
        Image img = new Image("/model/resources/thumbs_down.png");
        ImageView imgView = new ImageView(img);
        sadFaceButton.setGraphic(imgView);
        sadFaceButton.setMaxSize(50, 50);
    }*/

    //When the startButton is clicked, the scene changes to the "MainMenu" where the user selects the database they
    //wish to practice
    @FXML
    void startButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().databaseSelectMenu;
        Stage window = (Stage) startButton.getScene().getWindow();
        window.setScene(scene);/*
        Scene scene = micButton.getScene();
        scene.setRoot(Menu.DATABASESELECTMENU.loader().load());*/
    }

    @FXML
    void instructionsButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().instructionsMenu;
        Stage window = (Stage) instructionsButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void micButtonClicked(MouseEvent event) throws IOException {
//        Scene scene = SetUp.getInstance().microphoneCheckMenu;
//        Stage window = (Stage) micButton.getScene().getWindow();
//        window.setScene(scene);

        ProcessBuilder audioBuilder = new ProcessBuilder("/bin/bash", "-c", "bash myscript.sh");
        audioBuilder.start();
    }

    @FXML
    void sadFaceButtonClicked(MouseEvent event) throws IOException {
        SetUp.getInstance().audioRatingsController.setPreviousScene("startMenu");
        Scene scene = SetUp.getInstance().badRecordingsMenu;
        SetUp.getInstance().audioRatingsController.updateTextLog();
        Stage window = (Stage) sadFaceButton.getScene().getWindow();
        window.setScene(scene);
    }

//   Code for later?
//        Load the new scene
//        Scene scene = instructionsButton.getScene();
//        scene.setRoot(SetUp.Menu.INSTRUCTIONSMENU.loader().load();


}
