package model.resources;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class SetUp {

    //Single instance of NameSayer can be running at a time
    private static SetUp setUp;

    //Initialise controllers to allow for data to be passed between scenes
    PracticeMenuController practiceMenuController;
    AudioRatingsController audioRatingsController;
    CompareMenuController compareMenuController;
    StartMenuController startMenuController;

    //Scenes to load the fxml files to
    public Scene startMenu;
    Scene compareMenu;
    Scene databaseSelectMenu;
    Scene practiceMenu;
    Scene instructionsMenu;
    Scene audioRatingsMenu;
    Scene exitPracticeMenu;
    Scene trophiesMenu;

    private SetUp() throws IOException {

        //Load menus to be used throughout the program
        compareMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/CompareMenu.fxml")));
        databaseSelectMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/DatabaseSelectMenu.fxml")));
        practiceMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/PracticeMenu.fxml")));
        startMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/StartMenu.fxml")));
        audioRatingsMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/AudioRatings.fxml")));
        instructionsMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/InstructionsMenu.fxml")));
        exitPracticeMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/ExitPracticeMenu.fxml")));
        trophiesMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/TrophiesMenu.fxml")));

        //Load load menu
        startMenuLoader();
        mainMenuLoader();
        practiceMenuLoader();
        badRecordingsMenuLoader();
        instructionsMenuLoader();
        exitPracticeMenuLoader();
        compareMenuLoader();
        trophiesMenuLoader();

        //Add Theme.css to all scenes
        compareMenu.getStylesheets().add("/model/resources/Theme.css");
        databaseSelectMenu.getStylesheets().add("/model/resources/Theme.css");
        practiceMenu.getStylesheets().add("/model/resources/Theme.css");
        startMenu.getStylesheets().add("/model/resources/Theme.css");
        audioRatingsMenu.getStylesheets().add("/model/resources/Theme.css");
        instructionsMenu.getStylesheets().add("/model/resources/Theme.css");
        exitPracticeMenu.getStylesheets().add("/model/resources/Theme.css");
        trophiesMenu.getStylesheets().add("/model/resources/Theme.css");

    }

    //Constructor implementing Singleton pattern to create one instance of SetUp class where different scenes are created
    public static SetUp getInstance() throws IOException {
        if (setUp == null){
            setUp = new SetUp();
        }
        return setUp;
    }

    //Methods to load FXML files to scenes

    private void startMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/StartMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/StartMenu.fxml"));
        startMenu = new Scene(loader.load());
        startMenuController = loader.getController();
    }

    private void trophiesMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/TrophiesMenu.fxml"));
        trophiesMenu = new Scene(loader.load());
       TrophiesController trophiesController = loader.getController();
    }

    private void practiceMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/PracticeMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/PracticeMenu.fxml"));
        practiceMenu = new Scene(loader.load());
        practiceMenuController = loader.getController();
    }

    private void mainMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/DatabaseSelectMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/DatabaseSelectMenu.fxml"));
        databaseSelectMenu = new Scene(loader.load());
        DatabaseSelectMenuController databaseSelectMenuController = loader.getController();
    }

    private void badRecordingsMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/AudioRatings.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/AudioRatings.fxml"));
        audioRatingsMenu = new Scene(loader.load());
        audioRatingsController = loader.getController();
    }

    private void instructionsMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/InstructionsMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/InstructionsMenu.fxml"));
        instructionsMenu = new Scene(loader.load());
        InstructionsMenuController instructionsMenuController = loader.getController();
    }

    private void exitPracticeMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/ExitPracticeMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/ExitPracticeMenu.fxml"));
        exitPracticeMenu = new Scene(loader.load());
        ExitPracticeMenuController exitPracticeMenuController = loader.getController();
    }

    private void compareMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/CompareMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/CompareMenu.fxml"));
        compareMenu = new Scene(loader.load());
        compareMenuController = loader.getController();
    }
}
