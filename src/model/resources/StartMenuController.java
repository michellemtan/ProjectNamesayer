package model.resources;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StartMenuController {

    @FXML private Button micButton;
    @FXML private Button ratingsButton;
    @FXML private Button startButton;
    @FXML private Button instructionsButton;
    private Stage stage = new Stage();

    //TODO check the name folder is in right place

    public void setUp() {
        File f = new File(System.getProperty("user.dir") + "/names");
        if(!f.exists() || !f.isDirectory()) {
            BorderPane pane = new BorderPane();
            pane.setStyle("-fx-background-image: none");
            Label infoLabel = new Label("You will not be able to use Name Sayer without the default database, a folder called 'names' in the same directory as the jar file. To fix this, move the names databse to the correct location or add a new database from the settings menu.");
            infoLabel.setWrapText(true);
            Label titleLabel = new Label("Default Database Missing!");
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
            Button okButton = new Button("OK");

            okButton.setOnAction(e -> stage.close());

            pane.setCenter(infoLabel);
            pane.setTop(titleLabel);
            pane.setBottom(okButton);
            pane.setMaxSize(400, 150);
            pane.setMinSize(400, 150);

            Scene scene = new Scene(pane);
            scene.getStylesheets().add("/model/resources/Theme.css");
            stage.setScene(scene);
            stage.getIcons().add(new Image("/model/resources/images/icon.png"));
            stage.setTitle("Database not present");
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.show();
        }
    }

    @FXML
    void instructionsButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().instructionsMenu;
        Stage window = (Stage) instructionsButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void micButtonClicked() throws IOException {
        SetUp.getInstance().microphoneController.setPreviousScene("startMenu");
        Scene scene = SetUp.getInstance().microphoneMenu;
        Stage window = (Stage) micButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void ratingsButtonClicked() throws IOException {
        SetUp.getInstance().audioRatingsController.setPreviousScene("startMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) ratingsButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void startButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().databaseSelectMenu;
        Stage window = (Stage) startButton.getScene().getWindow();
        window.setScene(scene);
    }
}
