package ru.prolog.syntaxmodel.tree.semantics.attributes.errors;

/**
 * Не найдено объявление используемого предиката, функтора или типа данных
 */
public class DeclarationNotFoundError extends AbstractSemanticError {
    public DeclarationNotFoundError(String message) {
        super(message);
    }
}
