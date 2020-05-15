package ru.prolog.syntaxmodel.tree.semantics.attributes.errors;

/**
 * Неправильное количество аргументов у функтора
 */
public class WrongFunctorArgsCountError extends AbstractSemanticError {
    public WrongFunctorArgsCountError(String message) {
        super(message);
    }
}
