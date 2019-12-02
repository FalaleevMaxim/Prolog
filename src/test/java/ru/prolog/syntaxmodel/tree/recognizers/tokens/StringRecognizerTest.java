package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.Token;

import static org.junit.Assert.*;

public class StringRecognizerTest {
    private final StringRecognizer recognizer = new StringRecognizer();

    @Test
    public void recognizeBaseTest() {
        String testString = "\"1q2w3e_ASD\"";
        Token result = recognizer.recognize(testString);
        assertEquals(testString, result.getText());
        assertFalse(result.isPartial());

        result = recognizer.recognize(testString + "asd");
        assertEquals(testString, result.getText());
        assertFalse(result.isPartial());
    }

    @Test
    public void recognizeEmptyTest() {
        String testString = "\"\"asd";
        Token result = recognizer.recognize(testString);
        assertEquals(2, result.getText().length());
        assertFalse(result.isPartial());
    }

    @Test
    public void recognizeSpecialsTest() {
        String testString = "\"\\n\\r\\t\\ufa12\\\\ \\\" \"";
        Token result = recognizer.recognize(testString);
        assertEquals(testString, result.getText());
        assertFalse(result.isPartial());
        result = recognizer.recognize(testString + "asd");
        assertEquals(testString, result.getText());
        assertFalse(result.isPartial());
    }

    @Test
    public void recognizeWrongSpecialTest() {
        String testString = "\"\\kasd\"";
        Token result = recognizer.recognize(testString);
        assertEquals(testString, result.getText());
        assertTrue(result.isPartial());
        result = recognizer.recognize(testString + "asd");
        assertEquals(testString, result.getText());
        assertTrue(result.isPartial());
    }

    @Test
    public void recognizeWrongUnicodeTest() {
        String testString = "\"\\u\"";
        Token result = recognizer.recognize(testString);
        assertEquals(testString, result.getText());
        assertTrue(result.isPartial());
        assertNotNull(result.getHint().errorText);

        testString = "\"\\u12qwe\"";
        result = recognizer.recognize(testString);
        assertEquals(testString, result.getText());
        assertTrue(result.isPartial());
        assertNotNull(result.getHint().errorText);
    }

    @Test
    public void recognizeNoClosingTest() {
        String testString = "\"";
        Token result = recognizer.recognize(testString);
        assertEquals(testString, result.getText());
        assertTrue(result.isPartial());
        assertNotNull(result.getHint().errorText);

        testString = "\"qwe";
        result = recognizer.recognize(testString);
        assertEquals(testString, result.getText());
        assertTrue(result.isPartial());
        assertNotNull(result.getHint().errorText);
    }
}