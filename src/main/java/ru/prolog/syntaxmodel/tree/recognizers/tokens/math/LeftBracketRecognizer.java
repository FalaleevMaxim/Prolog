package ru.prolog.syntaxmodel.tree.recognizers.tokens.math;

import ru.prolog.syntaxmodel.tree.recognizers.tokens.AbstractKeywordRecognizer;

public class LeftBracketRecognizer extends AbstractKeywordRecognizer {
    public LeftBracketRecognizer() {
        super("(");
    }
}
