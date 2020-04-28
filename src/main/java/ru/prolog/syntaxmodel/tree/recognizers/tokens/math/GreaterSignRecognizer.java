package ru.prolog.syntaxmodel.tree.recognizers.tokens.math;

import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.TokenRecognizer;

public class GreaterSignRecognizer extends TokenRecognizer {

    @Override
    public Token recognize(CharSequence code) {
        if(code.length() == 0) return null;
        if(code.charAt(0) != '>') return null;
        // Не распознавать ">="
        if(code.length() > 1 && code.charAt(1) == '=') return null;

        return tokenOf(">");
    }
}
