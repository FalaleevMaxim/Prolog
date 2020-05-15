package ru.prolog.syntaxmodel.tree.semantics.attributes.errors;

/**
 * У предиката нет правил
 */
public class NoImplementationsError extends AbstractSemanticError {
    public NoImplementationsError(String message) {
        super(message);
    }
}
