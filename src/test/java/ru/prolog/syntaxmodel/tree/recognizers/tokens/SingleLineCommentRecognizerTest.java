package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.Token;

import static org.junit.Assert.assertEquals;

public class SingleLineCommentRecognizerTest {
    private final SingleLineCommentRecognizer recognizer = new SingleLineCommentRecognizer();

    @Test
    public void recognizeBaseTest() {
        Token result = recognizer.recognize("%");
        assertEquals(1, result.length());
        result = recognizer.recognize("%asd");
        assertEquals(4, result.length());
        result = recognizer.recognize("%asd\n");
        assertEquals(4, result.length());
        result = recognizer.recognize("%asd\r\n");
        assertEquals(4, result.length());
        result = recognizer.recognize("%asd\r\ncode");
        assertEquals(4, result.length());
    }
}