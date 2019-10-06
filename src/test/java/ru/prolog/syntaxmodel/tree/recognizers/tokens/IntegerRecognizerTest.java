package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import static org.junit.Assert.*;

public class IntegerRecognizerTest {
    private IntegerRecognizer recognizer = new IntegerRecognizer();

    @Test
    public void recognizeBaseTest() {
        RecognitionResult result = recognizer.recognize("123");
        assertEquals(3, result.recognizedText.length());
        assertTrue(result.success());
    }

    @Test
    public void recognizeStartTest() {
        RecognitionResult result = recognizer.recognize("123asd");
        assertEquals(3, result.recognizedText.length());
        assertTrue(result.success());
    }

    @Test
    public void recognizeWithSignTest() {
        RecognitionResult result = recognizer.recognize("+123asd");
        assertEquals(0, result.recognizedText.length());
        assertFalse(result.success());
        result = recognizer.recognize("-123asd");
        assertEquals(0, result.recognizedText.length());
        assertFalse(result.success());
    }

    @Test
    public void recognizeHexTest() {
        RecognitionResult result = recognizer.recognize("$f1asd");
        assertEquals("$f1a", result.recognizedText);
        assertTrue(result.success());
        result = recognizer.recognize("-$f1asd");
        assertFalse(result.success());
        result = recognizer.recognize("+$f1asd");
        assertFalse(result.success());
    }

    @Test
    public void recognizeWithPointTest() {
        RecognitionResult result = recognizer.recognize("123.0asd");
        assertEquals("123.0", result.recognizedText);
        assertTrue(result.success());
        result = recognizer.recognize("-123.asd");
        assertEquals(0, result.recognizedText.length());
        assertFalse(result.success());
        result = recognizer.recognize("+123.asd");
        assertEquals(0, result.recognizedText.length());
        assertFalse(result.success());
        result = recognizer.recognize("$f1.asd");
        assertEquals(3, result.recognizedText.length());
        assertFalse(result.success());
    }

    @Test
    public void recognizePartialTest() {
        RecognitionResult result = recognizer.recognize("$");
        assertEquals(1, result.recognizedText.length());
        assertTrue(result.partial);
        result = recognizer.recognize("$qwe");
        assertEquals(1, result.recognizedText.length());
        assertTrue(result.partial);
        result = recognizer.recognize("-$qwe");
        assertEquals(2, result.recognizedText.length());
        assertTrue(result.partial);
        result = recognizer.recognize("+$qwe");
        assertEquals(2, result.recognizedText.length());
        assertTrue(result.partial);
    }
}