package ru.prolog.syntaxmodel.tree.semantics.attributes.errors;

/**
 * Ошибка если существует функтор, предикат БД или тип данных с таким же именем
 */
public class DuplicateError extends AbstractSemanticError {
    public DuplicateError(String message) {
        super(message);
    }
}
