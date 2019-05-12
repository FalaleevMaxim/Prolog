package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import static org.junit.Assert.*;

public class StringRecognizerTest {
    private final StringRecognizer recognizer = new StringRecognizer();

    @Test
    public void recognizeBaseTest() {
        String testString = "\"1q2w3e_ASD\"";
        RecognitionResult result = recognizer.recognize(testString);
        assertEquals(testString.length(), result.recognized);
        assertFalse(result.partial);

        result = recognizer.recognize(testString + "asd");
        assertEquals(testString.length(), result.recognized);
        assertFalse(result.partial);
    }

    @Test
    public void recognizeEmptyTest() {
        String testString = "\"\"asd";
        RecognitionResult result = recognizer.recognize(testString);
        assertEquals(2, result.recognized);
        assertFalse(result.partial);
    }

    @Test
    public void recognizeSpecialsTest() {
        String testString = "\"\\n\\r\\t\\ufa12\\\\ \\\" \"";
        RecognitionResult result = recognizer.recognize(testString);
        assertEquals(testString.length(), result.recognized);
        assertFalse(result.partial);
        result = recognizer.recognize(testString + "asd");
        assertEquals(testString.length(), result.recognized);
        assertFalse(result.partial);
    }

    @Test
    public void recognizeWrongSpecialTest() {
        String testString = "\"\\kasd\"";
        RecognitionResult result = recognizer.recognize(testString);
        assertEquals(testString.length(), result.recognized);
        assertTrue(result.partial);
        result = recognizer.recognize(testString + "asd");
        assertEquals(testString.length(), result.recognized);
        assertTrue(result.partial);
    }

    @Test
    public void recognizeWrongUnicodeTest() {
        String testString = "\"\\u\"";
        RecognitionResult result = recognizer.recognize(testString);
        assertEquals(testString.length(), result.recognized);
        assertTrue(result.partial);
        assertNotNull(result.hint.errorText);

        testString = "\"\\u12qwe\"";
        result = recognizer.recognize(testString);
        assertEquals(testString.length(), result.recognized);
        assertTrue(result.partial);
        assertNotNull(result.hint.errorText);
    }
}