package ru.prolog.syntaxmodel.tree.semantics.attributes.errors;

import ru.prolog.syntaxmodel.tree.semantics.attributes.AbstractSemanticAttribute;

public abstract class AbstractSemanticError extends AbstractSemanticAttribute {
    private final String message;

    protected AbstractSemanticError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
