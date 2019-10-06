package ru.prolog.syntaxmodel.tree.recognizers.tokens.keywords;

import ru.prolog.syntaxmodel.tree.recognizers.tokens.AbstractKeywordRecognizer;

/**
 * Токен анонимной переменной.
 */
public class AnonymousVariableRecognizer extends AbstractKeywordRecognizer {
    public AnonymousVariableRecognizer() {
        super("_");
    }
}
