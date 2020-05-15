package ru.prolog.syntaxmodel.tree.semantics.attributes.errors;

/**
 * Предикат совпадает со встроенным предикатом
 */
public class IllegalPredicateError extends AbstractSemanticError {
    public IllegalPredicateError(String message) {
        super(message);
    }
}
