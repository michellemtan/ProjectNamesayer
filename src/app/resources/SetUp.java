package app.resources;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class SetUp {

    //Single instance of NameSayer can be running at a time
    private static SetUp setUp;

    //Initialise controllers to allow for data to be passed between scenes
    PractiseMenuController practiceMenuController;
    AudioRatingsController audioRatingsController;
    CompareMenuController compareMenuController;
    public StartMenuController startMenuController;
    EnterNamesController enterNamesController;
    MicrophoneController microphoneController;
    NamesListController namesListController;
    public SettingsMenuController settingsMenuController;
    ListenMenuController listenMenuController;

    //Scenes to load the fxml files to
    public Scene startMenu;
    Scene compareMenu;
    Scene practiceMenu;
    Scene instructionsMenu;
    Scene audioRatingsMenu;
    Scene exitPracticeMenu;
    Scene enterNamesMenu;
    Scene microphoneMenu;
    Scene namesListMenu;
    private Scene missingDB;
    Scene settingsMenu;
    Scene listenMenu;

    private SetUp() throws IOException {

        //Load scenes to be used in the program
        startMenu = startMenuLoader();
        compareMenu = compareMenuLoader();
        practiceMenu = practiceMenuLoader();
        audioRatingsMenu = audioRatingsMenuLoader();
        instructionsMenu = instructionsMenuLoader();
        exitPracticeMenu = exitPracticeMenuLoader();
        enterNamesMenu = enterNamesLoader();
        microphoneMenu = microphoneMenuLoader();
        namesListMenu = namesListMenuLoader();
        missingDB = missingDBLoader();
        settingsMenu = settingsMenuLoader();
        listenMenu = listenMenuLoader();

        //Add Theme.css to all scenes
        changeTheme(settingsMenuController.getTheme());
    }

    //Change theme method, invoked from settings menu upon choosing new theme
    void changeTheme(String themeURL) {
        settingsMenu.getStylesheets().clear();
        compareMenu.getStylesheets().clear();
        practiceMenu.getStylesheets().clear();
        startMenu.getStylesheets().clear();
        audioRatingsMenu.getStylesheets().clear();
        instructionsMenu.getStylesheets().clear();
        exitPracticeMenu.getStylesheets().clear();
        enterNamesMenu.getStylesheets().clear();
        microphoneMenu.getStylesheets().clear();
        namesListMenu.getStylesheets().clear();
        missingDB.getStylesheets().clear();
        listenMenu.getStylesheets().clear();
        compareMenu.getStylesheets().add(themeURL);
        practiceMenu.getStylesheets().add(themeURL);
        startMenu.getStylesheets().add(themeURL);
        audioRatingsMenu.getStylesheets().add(themeURL);
        instructionsMenu.getStylesheets().add(themeURL);
        exitPracticeMenu.getStylesheets().add(themeURL);
        enterNamesMenu.getStylesheets().add(themeURL);
        microphoneMenu.getStylesheets().add(themeURL);
        namesListMenu.getStylesheets().add(themeURL);
        missingDB.getStylesheets().add(themeURL);
        settingsMenu.getStylesheets().add(themeURL);
        listenMenu.getStylesheets().add(themeURL);
    }

    //Constructor implementing Singleton pattern to create one instance of SetUp class where different scenes are created
    public static SetUp getInstance() throws IOException {
        if (setUp == null){
            setUp = new SetUp();
        }
        return setUp;
    }

    //Methods to load FXML files to scenes
    private Scene startMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/StartMenu.fxml"));
        startMenu = new Scene(loader.load());
        startMenuController = loader.getController();
        return startMenu;
    }

    private Scene listenMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/ListenMenu.fxml"));
        listenMenu = new Scene(loader.load());
        listenMenuController = loader.getController();
        return listenMenu;
    }

    private Scene settingsMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/Settings.fxml"));
        settingsMenu = new Scene(loader.load());
        settingsMenuController = loader.getController();
        return settingsMenu;
    }

    private Scene missingDBLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/MissingDB.fxml"));
        missingDB = new Scene(loader.load());
        return missingDB;
    }

    private Scene practiceMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/PracticeMenu.fxml"));
        practiceMenu = new Scene(loader.load());
        practiceMenuController = loader.getController();
        return practiceMenu;
    }

    private Scene audioRatingsMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/AudioRatings.fxml"));
        audioRatingsMenu = new Scene(loader.load());
        audioRatingsController = loader.getController();
        return audioRatingsMenu;
    }

    private Scene instructionsMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/InstructionsMenu.fxml"));
        instructionsMenu = new Scene(loader.load());
        return instructionsMenu;
    }

    private Scene exitPracticeMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/ExitPracticeMenu.fxml"));
        exitPracticeMenu = new Scene(loader.load());
        return exitPracticeMenu;
    }

    private Scene compareMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/CompareMenu.fxml"));
        compareMenu = new Scene(loader.load());
        compareMenuController = loader.getController();
        return compareMenu;
    }

    private Scene enterNamesLoader() throws  IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/EnterNamesMenu.fxml"));
        enterNamesMenu = new Scene(loader.load());
        enterNamesController = loader.getController();
        return enterNamesMenu;
    }

    private Scene microphoneMenuLoader() throws  IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/MicrophoneMenu.fxml"));
        microphoneMenu = new Scene(loader.load());
        microphoneController = loader.getController();
        return microphoneMenu;
    }

    private Scene namesListMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app/views/NamesListMenu.fxml"));
        namesListMenu = new Scene(loader.load());
        namesListController = loader.getController();
        return namesListMenu;
    }
}