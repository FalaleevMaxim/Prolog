package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.Token;

import static org.junit.Assert.*;

public class MultiLineCommentRecognizerTest {
    private final MultiLineCommentRecognizer recognizer = new MultiLineCommentRecognizer();

    @Test
    public void recognizeBaseTest() {
        Token result = recognizer.recognize("/**/");
        assertEquals(4, result.length());
        result = recognizer.recognize("/*asd*/");
        assertEquals(7, result.length());
        result = recognizer.recognize("/*asd*/");
        assertEquals(7, result.length());
        result = recognizer.recognize("/**asd**/");
        assertEquals(9, result.length());
        result = recognizer.recognize("/*\n*\nasd\n*\n*/");
        assertEquals(13, result.length());

        result = recognizer.recognize("/*asd");
        assertNotNull(result);
        assertEquals(5, result.length());
        assertTrue(result.isPartial());

        result = recognizer.recognize("/*a /*sd*/");
        assertNotNull(result);
        assertEquals(10, result.length());
        assertTrue(result.isPartial());

        result = recognizer.recognize("/*/*asd*/*/");
        assertEquals(11, result.length());

        result = recognizer.recognize("/*a\n/*s*/\nd*/");
        assertEquals(13, result.length());
    }
}
