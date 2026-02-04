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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.LineUnavailableException;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {
    /** will store microphone recording into this file. */
    private File wavFile = new File("recording_buffer.wav");
    /** audio format object. */
    private AudioFormat format;
    /** API for microphone line. */
    private TargetDataLine line;
    /** Microphone sample rate. */
    private final float sampleRate = 48000;
    /** Microphone sample rate size. */
    private final int sampleSizeInBits = 16;
    /** Microphone number of channels. */
    private final int channels = 2;
    /** Microphone format option. */
    private final boolean signed = true;
    /** Microphone format option. */
    private final boolean bigEndian = true;
    /**
     *  * Constructor to build microphone format object.
     */
    public AudioRecorder() {
        format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
    }

    /**
     * Start microphone recording.
     */
    void startRecording() {
        try {
            DataLine.Info info = new DataLine.Info(
                    TargetDataLine.class, format);

            // check if supported
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            AudioInputStream audioStream = new AudioInputStream(line);
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wavFile);

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Stop microphone recording.
     */
    void stopRecording() {
        line.stop();
        line.close();
    }
}
