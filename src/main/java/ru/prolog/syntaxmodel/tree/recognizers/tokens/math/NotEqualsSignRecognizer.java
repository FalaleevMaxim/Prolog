package ru.prolog.syntaxmodel.tree.recognizers.tokens.math;

import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.TokenRecognizer;

public class NotEqualsSignRecognizer extends TokenRecognizer {

    @Override
    public Token recognize(CharSequence code) {
        if(matchText(code, "<>")) return tokenOf("<>");
        if(matchText(code, "><")) return tokenOf("><");
        return null;
    }
}
