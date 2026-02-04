package org.example;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.vosk.LogLevel;
import org.vosk.Recognizer;
import org.vosk.LibVosk;
import org.vosk.Model;

public class Main {
    public static void main(String[] argv) throws IOException, UnsupportedAudioFileException {
        AudioRecorder recorder = new AudioRecorder();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Press ENTER to start recording...");
        scanner.nextLine();

        // Thread for recording sound
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    scanner.nextLine()
;                   Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.stopRecording();
                System.out.println("Processing speech to text...");
            }
        });


        stopper.start();
        System.out.println("Recording started,");
        System.out.println("Press ENTER to stop recording...");
        recorder.startRecording();

        LibVosk.setLogLevel(LogLevel.DEBUG);

        try (Model model = new Model("vosk-model-en-us-0.22");
             InputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream("recording_buffer.wav")));
             Recognizer recognizer = new Recognizer(model, 16000)) {

            int nbytes;
            byte[] b = new byte[4096];
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