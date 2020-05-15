package ru.prolog.syntaxmodel.tree.semantics.attributes.warnings;

import ru.prolog.syntaxmodel.tree.semantics.attributes.AbstractSemanticAttribute;

public class AbstractSemanticWarning extends AbstractSemanticAttribute {
    private final String message;

    protected AbstractSemanticWarning(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
