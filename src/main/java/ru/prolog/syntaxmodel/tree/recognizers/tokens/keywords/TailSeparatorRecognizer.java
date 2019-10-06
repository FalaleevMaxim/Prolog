package ru.prolog.syntaxmodel.tree.recognizers.tokens.keywords;

import ru.prolog.syntaxmodel.tree.recognizers.tokens.AbstractKeywordRecognizer;

public class TailSeparatorRecognizer extends AbstractKeywordRecognizer {
    public TailSeparatorRecognizer() {
        super("|");
    }
}
