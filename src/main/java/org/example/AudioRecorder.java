package org.example;

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
    File wavFile = new File("recording_buffer.wav");
    AudioFormat format;
    TargetDataLine line;

    public AudioRecorder() {
        float sampleRate = 48000;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
    }

    void startRecording() {
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

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

    void stopRecording() {
        line.stop();
        line.close();
    }
}