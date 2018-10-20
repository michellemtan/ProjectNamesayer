package model.resources;

import javafx.scene.media.Media;

import java.io.File;
import java.io.IOException;

public class Creation {

    private String name;
    private Media media;
    private Media defaultMedia = null;
    private Media highestRatedMedia = null;

    public Creation(String n, Media m) {
        name = n;
        media = m;
    }

    public String getFullName() {
        return this.name;
    }

    public String getFirstName(){
        String firstName = name.split("[- ]")[0];
        return firstName;
    }

    //This method returns the default media file
    public Media getMedia(){
        if (defaultMedia == null){
            return defaultMedia;
        } else if (highestRatedMedia == null){
            return highestRatedMedia;
        } else {
            return media;
        }
    }

    public Media getFirstNameMedia() throws IOException {
        String pathToDB = SetUp.getInstance().settingsMenuController.getPathToDB();
        String folderName = pathToDB + "/" + this.getFirstName() + "/";
        File[] listFiles = new File(folderName).listFiles();
        Media firstNameMedia = new Media(listFiles[0].toURI().toString());
        return firstNameMedia;
    }

    //This method sets the highest rated media file
    public void setHighestRateMedia(Media m){
        highestRatedMedia = m;
    }


    //This method sets the default media file
    public void setDefaultMedia(Media m){
        defaultMedia = m;
    }

}