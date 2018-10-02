package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProcessor {

    private String pathToDB;

    public DatabaseProcessor(String path) {
        pathToDB = path;
    }

    public void processDB() {
        File dir = new File(pathToDB);
        File[] directoryListing = dir.listFiles();
        //Create folder for untrimmed audio files
        new File(pathToDB + "/uncut_files").mkdir();

        if(directoryListing != null) {
            for(File file : directoryListing) {
                if(!file.isDirectory()) {
                    //Get name from file name
                    String name = file.getName().substring(file.getName().lastIndexOf("_")+1, file.getName().length()-4);
                    //Create folder of name
                    new File(pathToDB + "/" + name).mkdir();
                    //Trim audio into newly created folder
                    String command = "ffmpeg -y -i " + file.getPath() + " -af silenceremove=1:0:-35dB " + pathToDB + "/" + name + "/" + file.getName();
                    trimAudio(command);
                    file.renameTo(new File(pathToDB + "/uncut_files/" + file.getName()));
                }
            }
        }

        //Code to deal with same names, case insensitive (chen = Chen)
        File[] namesListing = dir.listFiles();
        List<String> names = new ArrayList<>();
        //Create list of strings of names
        for(File file : namesListing) {
            names.add(file.getName());
        }
        //Iterate through names, and if there's a match then move into new upCased directory and delete old one
        for(String name : names) {
            if(containsCaseInsensitive(name, names)) {
                String upCased = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                new File(pathToDB + "/" + upCased).mkdir();
                File nameDir = new File(pathToDB + "/" + name);
                File[] subFiles = nameDir.listFiles();
                for(File file : subFiles) {
                    file.renameTo(new File(pathToDB + "/" + upCased + "/" + file.getName()));
                }
                nameDir.delete();
            }
        }

    }

    /**@param toCompare the String that we're checking is in the supplied list
     * @param list the supplied list
     * @return boolean value indicating if the String is in the List
     */
    private boolean containsCaseInsensitive(String toCompare, List<String> list){
        for (String string : list){
            if (string.equalsIgnoreCase(toCompare) && !string.equals(toCompare)){
                return true;
            }
        }
        return false;
    }

    /**
     * Code to trim audio's silence via bash ffmpeg command
     * @param command bash command to be run
     */
    private void trimAudio(String command) {
        try {
            ProcessBuilder builderRecord = new ProcessBuilder("/bin/bash", "-c", command);
            Process process = builderRecord.start();

            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();
            BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
            String line;
            while ((line = stdoutBuffered.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ignored) {
        }
    }
    //ffmpeg -y -i path/to/in.wav -af silenceremove=1:0:-35dB path/to/out.wav for removing silence
}
