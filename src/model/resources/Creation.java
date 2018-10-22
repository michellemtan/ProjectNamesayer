package model.resources;

import javafx.scene.media.Media;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Creation {

    private HashMap<String, Media> fullNameHashMap;
    private String name;
    private List<String> nameList;
    private double creationLength = 0.0;
    private double firstNameLength = 0.0;

    Creation(String n) throws IOException, UnsupportedAudioFileException {
        name = n;

        String pathToDB = SetUp.getInstance().settingsMenuController.getPathToDB();
        String[] split = name.replaceAll("- ", " ").split("[\\s]");
        nameList = new ArrayList<>();
        fullNameHashMap = new HashMap<>();

        for (int i=0; i<split.length; i++) {
            nameList.add(split[i]);
            //Set up the hash map to contain the default names
            String folderName = pathToDB + "/" + split[i] + "/";
            File[] listFiles = new File(folderName).listFiles();


            //Get audio file length and sum so creation length indicates length of entire creation
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(listFiles)[0]);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            creationLength += (frames+0.0) / format.getFrameRate() + 0.2; //Add 0.2 to account for running code difference
            if(i==0) {
                firstNameLength += (frames+0.0) / format.getFrameRate() + 0.2;
            }

            Media m = new Media(Objects.requireNonNull(listFiles)[0].toURI().toString());
            fullNameHashMap.put(split[i], m);
        }
    }

    double getFirstNameLength() {

        return firstNameLength;
    }

    double getCreationLength() {
        return creationLength;
    }

    String getFullName() {
        return this.name;
    }

    String getFirstName(){
        return name.split("[- ]")[0];
    }

    //This method returns the full name of media files
    List<Media> getFullNameMedia(){
        List<Media> mediaList = new ArrayList<>();
        for (String name: nameList){
            mediaList.add(fullNameHashMap.get(name));
        }
        return mediaList;
    }

    Media getFirstNameMedia() {
        String firstName = name.split("[- ]")[0];
        return fullNameHashMap.get(firstName);
    }

    //This method sets the highest rated media file
    public void setHighestRateMedia(String name, Media m){
        fullNameHashMap.put(name, m);
    }


    //This method sets the default media file
    void setDefaultMedia(String name, Media m){
        fullNameHashMap.put(name, m);
    }

    HashMap<String, Media> getFullNameHashMap() {
        return fullNameHashMap;
    }
}