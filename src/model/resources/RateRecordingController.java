package model.resources;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Arrays;

public class RateRecordingController extends AbstractController {

    @FXML private Label nameLabel;
    @FXML private Button okButton;
    @FXML private ComboBox<String> namesBox;
    @FXML private ComboBox<String> ratingsBox;
    @FXML private Button saveButton;
    @FXML private BorderPane bground;
    private Stage stage = null;
    private Tooltip customTooltip;

    @FXML
    void okButtonClicked() {
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    void saveButtonClicked(MouseEvent event) throws IOException {
        //Add rated name to audio ratings list / text file

        if (event.getButton() == MouseButton.PRIMARY) {

            String name = namesBox.getSelectionModel().selectedItemProperty().get();
            String rating = ratingsBox.getSelectionModel().selectedItemProperty().get();
            String ratedName = name.concat(": " + rating + "\n");
            SetUp.getInstance().audioRatingsController.addName(ratedName);

            //Show tool tip when a name has been rated
            showTooltip(stage, saveButton, "Rated name!", null);

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> customTooltip.hide());
            pause.play();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUp(String name) {
        bground.getStyleClass().add("root-clean");
        //Set up the name label
        nameLabel.setText("Rate " + name + "?");

        //Create list of ratings
        ObservableList<String> ratingChoices = FXCollections.observableArrayList();
        ratingChoices.add("★☆☆☆☆");
        ratingChoices.add("★★☆☆☆");
        ratingChoices.add("★★★☆☆");
        ratingChoices.add("★★★★☆");
        ratingChoices.add("★★★★★");

        //Set the ratings box with choices
        ratingsBox.getItems().setAll(ratingChoices);

        //Create list of names
        String[] split = name.replaceAll("-", " ").split("[\\s]");
        ObservableList<String> nameChoices = FXCollections.observableArrayList();
        nameChoices.addAll(Arrays.asList(split));

        //Set the names box with choices
        namesBox.getItems().setAll(nameChoices);

        //Set first item of each box to be default
        namesBox.getSelectionModel().selectFirst();
        ratingsBox.getSelectionModel().selectFirst();
    }

    //Source: https://stackoverflow.com/questions/17405688/javafx-activate-a-tooltip-with-a-button
    private void showTooltip(Stage owner, Button control, String tooltipText, ImageView tooltipGraphic) {
            Point2D p = control.localToScene(0.0, 0.0);

            customTooltip = new Tooltip();
            customTooltip.setText(tooltipText);

            control.setTooltip(customTooltip);
            customTooltip.setAutoHide(true);

            customTooltip.show(owner, p.getX()
                    + control.getScene().getX() + control.getScene().getWindow().getX(), p.getY()
                    + control.getScene().getY() + control.getScene().getWindow().getY());

            customTooltip.setAutoFix(true);
    }
}

