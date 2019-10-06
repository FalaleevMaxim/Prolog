package ru.prolog.syntaxmodel.tree.recognizers.tokens.keywords;

import ru.prolog.syntaxmodel.tree.recognizers.tokens.AbstractKeywordRecognizer;

public class IfSignRecognizer extends AbstractKeywordRecognizer {
    public IfSignRecognizer() {
        super(":-");
    }
}
