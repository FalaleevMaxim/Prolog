package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.Token;

import static org.junit.Assert.*;

public class RealRecognizerTest {
    private final RealRecognizer recognizer = new RealRecognizer();

    @Test
    public void recognizeBaseTest() {
        Token result = recognizer.recognize("123.45");
        assertEquals(6, result.getText().length());
    }

    @Test
    public void notRecognizeBeforePointTest() {
        Token result = recognizer.recognize("123.");
        assertNull(result);
    }

    @Test
    public void recognizeAfterPointTest() {
        Token result = recognizer.recognize(".45");
        assertEquals(3, result.getText().length());
    }

    @Test
    public void notRecognizeIntTest() {
        Token result = recognizer.recognize("123");
        assertNull(result);
    }

    @Test
    public void notRecognizePointTest() {
        Token result = recognizer.recognize(".");
        assertNull(result);
        recognizer.recognize(".qwe");
        assertNull(result);
    }
}