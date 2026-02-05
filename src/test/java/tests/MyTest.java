// Course: COMP490 - Software Development and Design
// Student: Luciano Nunes
// Professor: John Santore
// Assignment: Sprint 1 - Speech to Text project
// Purpose & Description: The purpose of this assignment is to
//      implement a decipher to convert baby language to arithmetic expressions

package tests;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

public class MyTest {
    public static final double maxLevenshteinDistanceTolerance = 30;

    // Uses Levenshtein distance as a metric to compare the two strings.
    @Test
    public void testModel() throws IOException, UnsupportedAudioFileException {
        // Build model
        LibVosk.setLogLevel(LogLevel.DEBUG);
        try (Model model = new Model("vosk-model-en-us-0.22");
             InputStream ais = AudioSystem.getAudioInputStream(
                     new BufferedInputStream(new FileInputStream("src/test/test_audio_sample.wav")));
             Recognizer recognizer = new Recognizer(model, 8000)) {

            // Use model to process audio
            int nbytes;
            byte[] b = new byte[4096];
            while ((nbytes = ais.read(b)) >= 0) {
                recognizer.acceptWaveForm(b, nbytes);
            }

            // Read sample testing text from file and get transcribed text from AI model.
            JSONObject jsonObject = new JSONObject(recognizer.getFinalResult());
            BufferedReader reader = new BufferedReader(new FileReader("src/test/java/correct_test.txt"));
            String correctSpeech = reader.readLine();
            String transcribedSpeech = jsonObject.getString("text");

            // Compute leverstein distance between both strings
            LevenshteinDistance LevenshteinInstance = new LevenshteinDistance();
            int distance = LevenshteinInstance.apply(correctSpeech, transcribedSpeech);

            // Assert distance is below tolerance.
            assert(distance <= maxLevenshteinDistanceTolerance);
        }
    }
}