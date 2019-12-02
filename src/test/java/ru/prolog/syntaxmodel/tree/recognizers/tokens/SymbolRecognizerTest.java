package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.Token;

import static org.junit.Assert.*;

public class SymbolRecognizerTest {
    private final SymbolRecognizer recognizer = new SymbolRecognizer();

    @Test
    public void recognizeBaseTest() {
        Token result = recognizer.recognize("q");
        assertEquals(1, result.getText().length());
        result = recognizer.recognize("qwert");
        assertEquals(5, result.getText().length());
        result = recognizer.recognize("qWert");
        assertEquals(5, result.getText().length());
        result = recognizer.recognize("qWe2rt");
        assertEquals(6, result.getText().length());
        result = recognizer.recognize("qWe_2rt");
        assertEquals(7, result.getText().length());
        result = recognizer.recognize("qWe_2rtЙч");
        assertEquals(9, result.getText().length());
        result = recognizer.recognize("яqWe_2rtйч");
        assertEquals(10, result.getText().length());

        result = recognizer.recognize("qWe_2rt+asd");
        assertEquals(7, result.getText().length());
        result = recognizer.recognize("qWe_2rt asd");
        assertEquals(7, result.getText().length());
        result = recognizer.recognize("qWe_2rt, asd");
        assertEquals(7, result.getText().length());
    }

    @Test
    public void recognizeUnderscoreTest() {
        Token result = recognizer.recognize("_");
        assertNull(result);
        result = recognizer.recognize("___");
        assertNull(result);
        result = recognizer.recognize("___qWe2rt");
        assertEquals(9, result.getText().length());
    }

    @Test
    public void notRecognizeStartNumberTest() {
        Token result = recognizer.recognize("2qwert");
        assertNull(result);
    }

    @Test
    public void notRecognizeStartCapitalTest() {
        Token result = recognizer.recognize("Qwert");
        assertNull(result);
        result = recognizer.recognize("Яwert");
        assertNull(result);
    }

    @Test
    public void notRecognizeKeywordsTest() {
        Token result = recognizer.recognize("predicates");
        assertNull(result);
    }
}