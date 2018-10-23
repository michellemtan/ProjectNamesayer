package app.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AudioRatingsController {

    private HashMap<String, String> ratingMap = new HashMap<>();
    @FXML private Button backButton;
    @FXML private TextArea textArea;
    private String previousScene = "";

    //Return the user to the appropriate menu based on where they came from
    @FXML
    void backButtonClicked() throws IOException {
        //Switch statement to return to correct previous scene
        switch (previousScene) {
            case "practiceMenu": {
                Scene scene = SetUp.getInstance().practiceMenu;
                Stage window = (Stage) backButton.getScene().getWindow();
                window.setScene(scene);
                break;
            }
            case "startMenu": {
                Scene scene = SetUp.getInstance().startMenu;
                Stage window = (Stage) backButton.getScene().getWindow();
                window.setScene(scene);
                break;
            }
            case "compareMenu": {
                Scene scene = SetUp.getInstance().compareMenu;
                Stage window = (Stage) backButton.getScene().getWindow();
                window.setScene(scene);
                break;
            }
            case "namesListMenu": {
                Scene scene = SetUp.getInstance().namesListMenu;
                Stage window = (Stage) backButton.getScene().getWindow();
                window.setScene(scene);
                break;
            }
            case "listenMenu": {
                Scene scene = SetUp.getInstance().listenMenu;
                Stage window = (Stage) backButton.getScene().getWindow();
                window.setScene(scene);
                break;
            }
        }
    }

    //Helper method to set previous scene. Invoked upon changing to this scene
    void setPreviousScene(String name) throws IOException {
        previousScene = name;
        updateTextLog();
    }

    //This method updates the text area to display contents of the audio ratings text file
    private void updateTextLog() throws IOException {

        //Read in the file containing the list of bad quality recordings
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("AudioRatings.txt")))) {
            StringBuilder fieldContent = new StringBuilder();
            readFile(reader);

            //Add the line to be displayed
            List<String> lineList = new ArrayList<>(ratingMap.values());

            //Sort the ratings alphabetically
            Collections.sort(lineList);

            //Output to text field and display
            for (String outputLine : lineList) {
                fieldContent.append(outputLine).append("\n");
            }

            //Ensure the textArea is not editable by the user
            textArea.setText(fieldContent.toString());
            textArea.setEditable(false);

        } catch (IOException e) {
            //If there are no bad recordings saved, create a new text file to store them
            File f = new File("AudioRatings.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
            bw.flush();
            bw.close();
        }
    }

    //This method is called when the clear button is pressed
    @FXML
    public void clearTextLog() throws IOException {
        File file = new File("AudioRatings.txt");
        PrintWriter writer = new PrintWriter(file);
        ratingMap = new HashMap<>();
        updateTextLog();
    }

    //This method is used to add names to the audio ratings text file
    void addName(String name) throws IOException {
        File f = new File("AudioRatings.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        bw.append(name);
        bw.flush();
        bw.close();
        updateTextLog();
    }

    private void readFile(BufferedReader reader ) throws IOException {
        //Read audio ratings text file
        String line;
        while ((line = reader.readLine()) != null) {
            //Concatenate each line of the file to the StringBuilder
            String name = line.substring(0, line.indexOf(":"));
            name = name.replaceAll(".wav", "");
            name = name.concat(".wav");
            ratingMap.put(name, line.trim());
        }
    }
}

