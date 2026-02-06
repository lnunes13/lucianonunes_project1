// Course: COMP490 - Software Development and Design
// Student: Luciano Nunes
// Professor: John Santore
// Assignment: Sprint 1 - Speech to Text project
// Purpose & Description: Implement recording of a speech to text
//    model that records microphone audio and converts it to text.

/**
 * This is the main package.
 */
package org.main;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONObject;
import org.vosk.Recognizer;
import org.vosk.Model;

public final class Main {
    /** Delay to prevent thread from consuming. */
    public static final int SLEEP_DELAY = 100;
    /** Microphone bit rate to be configured. */
    public static final int SAMPLE_RATE = 8000;
    /** Buffersize for recognizer. */
    public static final int BUFFER_SIZE = 4096;

    /**
     * Wrote this to stop checkstyle from complaining.
     */
    private Main() { }

    /**
    * Main function.
     *
     * @param argv command line arguments.
    */
    public static void main(final String[] argv)
            throws IOException, UnsupportedAudioFileException {

        // used to record microphone
        AudioRecorder recorder = new AudioRecorder();
        // used to require user to press enter
        Scanner scanner = new Scanner(System.in);

        // Thread for stopping recording after user presses enter
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    scanner.nextLine();
                    Thread.sleep(SLEEP_DELAY);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.stopRecording();
                System.out.println("Processing speech to text...");
            }
        });

        // prompt user to press ENTER
        System.out.println("Press ENTER to start recording...");
        scanner.nextLine();

        // start recording
        stopper.start();
        System.out.println("Recording started,");
        System.out.println("Press ENTER to stop recording...");
        recorder.startRecording();

        // Transcribe audio to text
        String transcribedText = voskTranscribeAudio("recording_buffer.wav");

        // Write text to file and save
        BufferedWriter writer = new BufferedWriter(
                new FileWriter("recorded_text.txt"));
        System.out.println("Transcribed text: " + transcribedText);
        writer.write(transcribedText);
        writer.close();
    }

    /**
     * Transcribes a given audio file to text.
     *
     * @param filepath path of the audio file.
     * @return transcribed text as a string.
     */
    public static String voskTranscribeAudio(final String filepath)
            throws IOException, UnsupportedAudioFileException {
        try (Model model = new Model("vosk-model-en-us-0.22");
             InputStream ais = AudioSystem.getAudioInputStream(
                     new BufferedInputStream(new FileInputStream(filepath)));
             Recognizer recognizer = new Recognizer(model, SAMPLE_RATE)) {

            // Use model to process audio
            int nbytes;
            byte[] b = new byte[BUFFER_SIZE];
            while ((nbytes = ais.read(b)) >= 0) {
                recognizer.acceptWaveForm(b, nbytes);
            }
            JSONObject jsonObject = new JSONObject(recognizer.getFinalResult());
            return jsonObject.getString("text");
        }
    }
}
