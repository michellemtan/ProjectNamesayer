package model.resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.IOException;

public class MicrophoneController {

    @FXML
    private Button backButton;

    @FXML
    private ProgressBar progressBar;

    private String previousScene = "";

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException {

        if (previousScene.equals("compareMenu")) {
            Scene scene = SetUp.getInstance().compareMenu;
            Stage window = (Stage) backButton.getScene().getWindow();
            window.setScene(scene);
        } else if (previousScene.equals("startMenu")) {
            Scene scene = SetUp.getInstance().startMenu;
            Stage window = (Stage) backButton.getScene().getWindow();
            window.setScene(scene);
        }
    }

    public void setPreviousScene(String name) throws IOException {
        previousScene = name;
        startMic();
    }

    public void startMic() {

        new Thread () {
            @Override
            public void run () {
                //Based on:  https://stackoverflow.com/questions/26574326/how-to-calculate-the-level-amplitude-db-of-audio-signal-in-java
                TargetDataLine line = null;
                AudioFormat format = new AudioFormat(44100f, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                int sample;
                byte[] readByte = new byte[2048];
                float[] sampleBytes = new float[1024];

                if (!AudioSystem.isLineSupported(info)) {
                    System.out.println("The line is not supported");
                }

                // Obtain and open the line
                try {
                    line = (TargetDataLine) AudioSystem.getLine(info);
                    line.open(format, 1024);
                } catch (
                        LineUnavailableException ex) {
                    System.out.println("The TargetDataLine is unavailable");
                    System.exit(-1);
                }

                line.start();

                //Read bytes by sampling audio data from target data line
                for (int byteSize; (byteSize = line.read(readByte, 0, readByte.length)) > -1; byteSize++ ) {

                    for (int i = 0, j= 0; i < byteSize; ) {
                        sample = 0;

                        //Get last 16 bits as sample of data line
                        sample = sample | readByte[i++] & 0xFF;
                        sample = sample | readByte[i++] << 8;
                        sampleBytes[j++] = sample / 32768f;
                    }

                    double progress = (double) calculateRMSLevel(sampleBytes);
                    progressBar.setProgress(progress);

                    Stage stage = (Stage) progressBar.getScene().getWindow();

                    if (backButton.isPressed() || !stage.isShowing()) {
                        line.close();
                        return;
                    }
                }
            }
        }.start();

    }

    private float calculateRMSLevel(float[] audioData) {
        float rms = 0f;

        for(float audioLevel : audioData) {
            rms = rms + (float)Math.pow(audioLevel, 2);
        }

        rms = (float)Math.sqrt(rms / audioData.length);
        return rms;
    }

}
