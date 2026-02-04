// Course: COMP490 - Software Development and Design
// Student: Luciano Nunes
// Professor: John Santore
// Assignment: Sprint 1 - Speech to Text project
// Purpose & Description: The purpose of this assignment is to
//      implement a decipher to convert baby language to arithmetic expressions

/**
 * This is the main package.
 */
package org.main;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.vosk.LogLevel;
import org.vosk.Recognizer;
import org.vosk.LibVosk;
import org.vosk.Model;

public final class Main {
    /** Delay to prevent thread from consuming. */
    public static final int SLEEP_DELAY = 100;
    /** Microphone bit rate to be configured. */
    public static final int SAMPLE_RATE = 48000;
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

        System.out.println("Press ENTER to start recording...");
        scanner.nextLine();

        // Thread for recording sound
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

        // start recording
        stopper.start();
        System.out.println("Recording started,");
        System.out.println("Press ENTER to stop recording...");
        // stop recording
        recorder.startRecording();

        // Run AI model to convert speech to text
        LibVosk.setLogLevel(LogLevel.DEBUG);

        try (Model model = new Model("vosk-model-en-us-0.22");
             InputStream ais = AudioSystem.getAudioInputStream(
                     new BufferedInputStream(Files.newInputStream(
                             Paths.get("recording_buffer.wav"))));
             Recognizer recognizer = new Recognizer(model, SAMPLE_RATE)) {

            int nbytes;
            byte[] b = new byte[BUFFER_SIZE];
            while ((nbytes = ais.read(b)) >= 0) {
                if (recognizer.acceptWaveForm(b, nbytes)) {
                    System.out.println(recognizer.getResult());
                } else {
                    System.out.println(recognizer.getPartialResult());
                }
            }

            System.out.println(recognizer.getFinalResult());
        }
    }
}
