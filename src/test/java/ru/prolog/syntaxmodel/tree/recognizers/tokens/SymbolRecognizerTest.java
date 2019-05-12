package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import static org.junit.Assert.*;

public class SymbolRecognizerTest {
    private final SymbolRecognizer recognizer = new SymbolRecognizer();

    @Test
    public void recognizeBaseTest() {
        RecognitionResult result = recognizer.recognize("q");
        assertEquals(1, result.recognized);
        result = recognizer.recognize("qwert");
        assertEquals(5, result.recognized);
        result = recognizer.recognize("qWert");
        assertEquals(5, result.recognized);
        result = recognizer.recognize("qWe2rt");
        assertEquals(6, result.recognized);
        result = recognizer.recognize("qWe_2rt");
        assertEquals(7, result.recognized);
        result = recognizer.recognize("qWe_2rtЙч");
        assertEquals(9, result.recognized);
        result = recognizer.recognize("яqWe_2rtйч");
        assertEquals(10, result.recognized);

        result = recognizer.recognize("qWe_2rt+asd");
        assertEquals(7, result.recognized);
        result = recognizer.recognize("qWe_2rt asd");
        assertEquals(7, result.recognized);
        result = recognizer.recognize("qWe_2rt, asd");
        assertEquals(7, result.recognized);
    }

    @Test
    public void recognizeUnderscoreTest() {
        RecognitionResult result = recognizer.recognize("_");
        assertEquals(0, result.recognized);
        result = recognizer.recognize("___");
        assertEquals(0, result.recognized);
        result = recognizer.recognize("___qWe2rt");
        assertEquals(9, result.recognized);
    }

    @Test
    public void notRecognizeStartNumberTest() {
        RecognitionResult result = recognizer.recognize("2qwert");
        assertEquals(0, result.recognized);
    }

    @Test
    public void notRecognizeStartCapitalTest() {
        RecognitionResult result = recognizer.recognize("Qwert");
        assertEquals(0, result.recognized);
        result = recognizer.recognize("Яwert");
        assertEquals(0, result.recognized);
    }

    @Test
    public void notRecognizeKeywordsTest() {
        RecognitionResult result = recognizer.recognize("predicates");
        assertEquals(0, result.recognized);
    }
}