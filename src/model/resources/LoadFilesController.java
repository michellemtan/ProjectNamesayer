package model.resources;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.resources.SetUp;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadFilesController {

    @FXML
    private Text backButton;

    @FXML
    private ListView<String> databaseNamesLIstView;

    @FXML
    private Button searchButton;

    @FXML
    private Button addButton;

    @FXML
    private ListView<String> practiceNamesListView;

    @FXML
    private Button practiceButton;

    @FXML
    private Button expandButton;

    @FXML
    private Button hideButton;

    @FXML
    private TextField textField;

    @FXML
    SplitPane splitPane;

    @FXML
    void addButtonClicked(MouseEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select text file");
        Stage fcStage = new Stage();
        File selectedFile = (File) fc.showOpenDialog(fcStage);
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        if (selectedFile != null) {
            textField.setText(selectedFile.toString());

            List<String> namesList = new ArrayList<>();

            //Read in the file containing the list of bad quality recordings
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                StringBuilder fieldContent = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    namesList.add(line);
                }

                practiceNamesListView.getItems().setAll(namesList);
                practiceNamesListView.refresh();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().databaseSelectMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void practiceButtonClicked(MouseEvent event) throws IOException {
        SetUp.getInstance().exitPracticeMenuController.setPreviousScene("loadFilesMenu");
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) practiceButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void searchButtonClicked(MouseEvent event) {

    }

    @FXML
    void expandButtonClicked(MouseEvent event) {
        splitPane.setDividerPositions(1);
    }

    @FXML
    void hideButtonClicked(MouseEvent event) {
        splitPane.setDividerPositions(0.005);
    }

}
