package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import static org.junit.Assert.*;

public class IntegerRecognizerTest {
    IntegerRecognizer recognizer = new IntegerRecognizer();

    @Test
    public void recognizeBaseTest() {
        RecognitionResult result = recognizer.recognize("123");
        assertEquals(3, result.recognized);
    }

    @Test
    public void recognizeStartTest() {
        RecognitionResult result = recognizer.recognize("123asd");
        assertEquals(3, result.recognized);
    }

    @Test
    public void recognizeWithSignTest() {
        RecognitionResult result = recognizer.recognize("+123asd");
        assertEquals(4, result.recognized);
        result = recognizer.recognize("-123asd");
        assertEquals(4, result.recognized);
    }

    @Test
    public void recognizeHexTest() {
        RecognitionResult result = recognizer.recognize("$f1asd");
        assertEquals(4, result.recognized);
        result = recognizer.recognize("-$f1asd");
        assertEquals(5, result.recognized);
        result = recognizer.recognize("+$f1asd");
        assertEquals(5, result.recognized);
    }

    @Test
    public void recognizeWithPointTest() {
        RecognitionResult result = recognizer.recognize("123.0asd");
        assertEquals(0, result.recognized);
        result = recognizer.recognize("-123.asd");
        assertEquals(0, result.recognized);
        result = recognizer.recognize("+123.asd");
        assertEquals(0, result.recognized);
        result = recognizer.recognize("$f1.asd");
        assertEquals(3, result.recognized);
    }

    @Test
    public void recognizePartialTest() {
        RecognitionResult result = recognizer.recognize("$");
        assertEquals(1, result.recognized);
        assertTrue(result.partial);
        result = recognizer.recognize("$qwe");
        assertEquals(1, result.recognized);
        assertTrue(result.partial);
        result = recognizer.recognize("-$qwe");
        assertEquals(2, result.recognized);
        assertTrue(result.partial);
        result = recognizer.recognize("+$qwe");
        assertEquals(2, result.recognized);
        assertTrue(result.partial);
    }
}