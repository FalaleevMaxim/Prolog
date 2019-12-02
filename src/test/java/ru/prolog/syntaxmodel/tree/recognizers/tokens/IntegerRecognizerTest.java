package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.Token;

import static org.junit.Assert.*;

public class IntegerRecognizerTest {
    private IntegerRecognizer recognizer = new IntegerRecognizer();

    @Test
    public void recognizeBaseTest() {
        Token result = recognizer.recognize("123");
        assertEquals(3, result.getText().length());
        assertFalse(result.isPartial());
    }

    @Test
    public void recognizeStartTest() {
        Token result = recognizer.recognize("123asd");
        assertEquals(3, result.getText().length());
        assertFalse(result.isPartial());
    }

    @Test
    public void notRcognizeWithSignTest() {
        Token result = recognizer.recognize("+123asd");
        assertNull(result);
        result = recognizer.recognize("-123asd");
        assertNull(result);
    }

    @Test
    public void recognizeHexTest() {
        Token result = recognizer.recognize("$f1asd");
        assertEquals("$f1a", result.getText());
        assertFalse(result.isPartial());
    }

    @Test
    public void recognizeWithPointTest() {
        Token result = recognizer.recognize("123.0asd");
        assertNull(result);
        result = recognizer.recognize("-123.asd");
        assertNull(result);
        result = recognizer.recognize("+123.asd");
        assertNull(result);
        result = recognizer.recognize("$f1.asd");
        assertEquals(3, result.getText().length());
        assertFalse(result.isPartial());
    }

    @Test
    public void recognizePartialTest() {
        Token result = recognizer.recognize("$");
        assertEquals(1, result.getText().length());
        assertTrue(result.isPartial());
        result = recognizer.recognize("$qwe");
        assertEquals(1, result.getText().length());
        assertTrue(result.isPartial());
        result = recognizer.recognize("-$qwe");
        assertNull(result);
        result = recognizer.recognize("+$qwe");
        assertNull(result);
    }
}