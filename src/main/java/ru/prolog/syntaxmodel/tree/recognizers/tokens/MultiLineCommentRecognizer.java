package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.recognizers.Hint;

import java.util.Collections;

public class MultiLineCommentRecognizer extends TokenRecognizer {
    @Override
    public Token recognize(CharSequence code) {
        if (code.length() < 2 || code.charAt(0) != '/' || code.charAt(1) != '*') return null;

        int level = 1;
        int i;
        for (i = 2; i < code.length() && level > 0;) {
            if (code.length() - i < 2) {
                return partialTokenOf(tokenText(code, code.length()),
                        new Hint("Missing closing */", Collections.singletonList("*/")));
            }
            if (code.charAt(i) == '/' && code.charAt(i + 1) == '*') {
                level++;
                i+=2;
            } else if (code.charAt(i) == '*' && code.charAt(i + 1) == '/') {
                level--;
                i+=2;
            } else {
                i++;
            }
        }
        if(level == 0) return tokenOf(tokenText(code, i));
        return partialTokenOf(tokenText(code, code.length()),
                new Hint("Missing closing */", Collections.singletonList("*/")));
    }
}
