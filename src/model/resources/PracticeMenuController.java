package model.resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PracticeMenuController {

    @FXML
    private Button playPauseButton;

    @FXML
    private Button playSingleButton;

    @FXML
    private Button shuffleButton;

    @FXML
    private Button compareButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ListView<String> creationsListView;

    @FXML
    private Label creationName;

    @FXML
    private Button backButton;

    @FXML
    private Button ratingsButton;

    @FXML
    private ContextMenu ratingsContext;

    @FXML
    private MenuItem audioRatings;


    private List<String> creationList;

    private String pathToDB;

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().exitPracticeMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void ratingsButtonClicked(MouseEvent event) throws IOException {
        String selectedName = creationsListView.getSelectionModel().getSelectedItem() + ".wav";

        if (event.getButton() == MouseButton.PRIMARY) {
            //Ask the user to rate their choice
            List<String> choices = new ArrayList<>();
            choices.add("★☆☆☆☆");
            choices.add("★★☆☆☆");
            choices.add("★★★☆☆");
            choices.add("★★★★☆");
            choices.add("★★★★★");
            ChoiceDialog<String> dialog = new ChoiceDialog<>("★☆☆☆☆", choices);
            dialog.setTitle("Recording Rating");
            dialog.setGraphic(null);
            dialog.setHeaderText("Rate " + selectedName + "?");
            dialog.setContentText("Select a rating:");

            //Get rating and format to string
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                try {
                    String rating = result.get();
                    String defaultName = selectedName.concat(": " + rating + "\n");
                    SetUp.getInstance().audioRatingsController.addName(defaultName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void audioRatingsPressed() throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("practiceMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void playButtonClicked(MouseEvent event) {

    }

    @FXML
    void playSingleButtonClicked(MouseEvent event) {

    }

    @FXML
    void compareButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().compareMenu;
        Stage window = (Stage) compareButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void shuffleButtonClicked(MouseEvent event) {

    }

    @FXML
    void audioRatingsPressed(ActionEvent event){

    }

    public void setUpList(List<String> list) throws IOException {
        creationList = list;
        pathToDB = SetUp.getInstance().databaseSelectMenuController.getPathToDB();
        playPauseButton.setDisable(false);
        if (creationList.size()<=1){
            playPauseButton.setDisable(true);
            shuffleButton.setDisable(true);
        }
        creationsListView.getItems().setAll(creationList);
        creationsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pathToDB = SetUp.getInstance().databaseSelectMenuController.getPathToDB();


        creationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            creationName.setText(creationsListView.getSelectionModel().getSelectedItem());
            if(creationsListView.getSelectionModel().getSelectedItems().size() != 1) {

            } else {
                playSingleButton.setDisable(false);

            }
        });

        setUpTitle();
    }

    private void setUpTitle(){
        if (creationList!=null){
            creationsListView.getSelectionModel().select(0);
            creationName.setText(creationList.get(0));
        }
    }
}


