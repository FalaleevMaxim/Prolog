package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import org.junit.Test;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import static org.junit.Assert.*;

public class SingleLineCommentRecognizerTest {
    private final SingleLineCommentRecognizer recognizer = new SingleLineCommentRecognizer();

    @Test
    public void recognizeBaseTest() {
        RecognitionResult result = recognizer.recognize("//");
        assertEquals(2, result.recognized);
        result = recognizer.recognize("//asd");
        assertEquals(5, result.recognized);
        result = recognizer.recognize("//asd\n");
        assertEquals(5, result.recognized);
        result = recognizer.recognize("//asd\r\n");
        assertEquals(5, result.recognized);
        result = recognizer.recognize("//asd\r\ncode");
        assertEquals(5, result.recognized);
    }

    @Test
    public void notRecognizeOneSlashTest() {
        RecognitionResult result = recognizer.recognize("/");
        assertEquals(0, result.recognized);
        result = recognizer.recognize("/asd");
        assertEquals(0, result.recognized);
        result = recognizer.recognize("/\n");
        assertEquals(0, result.recognized);
        result = recognizer.recognize("/\r\n");
        assertEquals(0, result.recognized);
        result = recognizer.recognize("/ /asd\r\ncode");
        assertEquals(0, result.recognized);
    }
}