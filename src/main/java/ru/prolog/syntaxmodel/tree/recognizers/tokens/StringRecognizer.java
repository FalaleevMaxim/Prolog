package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.recognizers.Hint;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult.NOT_RECOGNIZED;

/**
 * Токен строки в кавычках
 */
public class StringRecognizer extends TokenRecognizer {
    private static final String HEX = "[0-9a-fA-F]";
    private static final String END = String.format("(\\\\\"|\\\\t|\\\\r|\\\\n|\\\\u%s%s%s%s)", HEX, HEX, HEX, HEX);
    public static final String REGEXP = "^\"(" + END + "|[^\"\\\\])*\"";
    public static final Pattern PATTERN = Pattern.compile(REGEXP);

    @Override
    public RecognitionResult recognize(CharSequence code) {
        return recognizeString(code);
        /*Matcher matcher = PATTERN.matcher(code);
        if (!matcher.find()) return NOT_RECOGNIZED;
        return new RecognitionResult(matcher.end());*/
    }

    private RecognitionResult recognizeString(CharSequence code) {
        if (code.length() == 0 || code.charAt(0) != '"') return NOT_RECOGNIZED;

        int i = 1;
        int errorPos = -1;
        for (; i < code.length(); i++) {
            if (code.charAt(i) == '"') {
                i++;
                break;
            }
            if (code.charAt(i) == '\\') {
                int special = matchSpecialCharacter(code.subSequence(i, code.length() - 1));
                if (special == 0) {
                    if (errorPos < 0) errorPos = i;
                }
                i += special;
            }
        }
        if (i == code.length() && code.charAt(i - 1) != '"')
            return new RecognitionResult(i, true, new Hint("No closing '\"'", code.toString() + '"'));
        if (errorPos >= 0) return new RecognitionResult(i, true,
                new Hint("Wrong character " + code.subSequence(errorPos, errorPos + 1 > code.length() ? code.length() : errorPos + 2), null));
        return new RecognitionResult(i);
    }

    private int matchSpecialCharacter(CharSequence code) {
        final String HEX = "[0-9a-fA-F]";
        final String END = String.format("^(\\\\\\\\|\\\\\"|\\\\t|\\\\r|\\\\n|\\\\u%s%s%s%s)", HEX, HEX, HEX, HEX);
        Matcher matcher = Pattern.compile(END).matcher(code);
        if (!matcher.find()) return 0;
        return matcher.end();
    }
}
