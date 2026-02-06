// Course: COMP490 - Software Development and Design
// Student: Luciano Nunes
// Professor: John Santore
// Assignment: Sprint 1 - Speech to Text project
// Purpose & Description: The purpose of this assignment is to
//      implement a decipher to convert baby language to arithmetic expressions

package tests;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.jupiter.api.Test;
import org.main.Main;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MyTest {
    public static final double maxLevenshteinDistanceTolerance = 30;

    // Uses Levenshtein distance as a metric to compare the two strings.
    @Test
    public void testModel() throws IOException, UnsupportedAudioFileException {
        // Load test text from file
        BufferedReader reader = new BufferedReader(new FileReader("src/test/java/correct_test.txt"));
        String correctSpeech = reader.readLine();

        // Use AI model to transcribe text
        String transcribedSpeech = Main.voskTranscribeAudio("src/test/test_audio_sample.wav");

        // Compute Levenshtein distance between both strings
        LevenshteinDistance LevenshteinInstance = new LevenshteinDistance();
        int distance = LevenshteinInstance.apply(correctSpeech, transcribedSpeech);

        // Assert distance is below tolerance.
        assert(distance <= maxLevenshteinDistanceTolerance);
    }
}