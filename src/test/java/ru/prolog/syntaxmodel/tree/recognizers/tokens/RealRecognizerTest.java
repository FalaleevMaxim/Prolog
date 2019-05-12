package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import static org.junit.Assert.*;

public class RealRecognizerTest {
    private final RealRecognizer recognizer = new RealRecognizer();

    @Test
    public void recognizeBaseTest() {
        RecognitionResult result = recognizer.recognize("123.45");
        assertEquals(6, result.recognized);
    }

    @Test
    public void recognizeBeforePointTest() {
        RecognitionResult result = recognizer.recognize("123.");
        assertEquals(4, result.recognized);
    }

    @Test
    public void recognizeAfterPointTest() {
        RecognitionResult result = recognizer.recognize(".45");
        assertEquals(3, result.recognized);
    }

    @Test
    public void notRecognizeIntTest() {
        RecognitionResult result = recognizer.recognize("123");
        assertEquals(0, result.recognized);
    }

    @Test
    public void recognizePointPartialTest() {
        RecognitionResult result = recognizer.recognize(".");
        assertEquals(1, result.recognized);
        assertTrue(result.partial);
        assertNotNull(result.hint.errorText);
        result = recognizer.recognize(".qwe");
        assertEquals(1, result.recognized);
        assertTrue(result.partial);
        assertNotNull(result.hint.errorText);
    }
}