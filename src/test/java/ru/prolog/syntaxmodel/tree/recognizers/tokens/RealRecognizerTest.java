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
    public void recognizeBeforePointTest() {
        Token result = recognizer.recognize("123.");
        assertEquals(4, result.getText().length());
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
    public void recognizePointPartialTest() {
        Token result = recognizer.recognize(".");
        assertEquals(1, result.getText().length());
        assertTrue(result.isPartial());
        assertNotNull(result.getHint().errorText);
        result = recognizer.recognize(".qwe");
        assertEquals(1, result.getText().length());
        assertTrue(result.isPartial());
        assertNotNull(result.getHint().errorText);
    }
}