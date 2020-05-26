package ru.prolog.syntaxmodel.tree.semantics.attributes.errors;

import ru.prolog.syntaxmodel.tree.Node;

/**
 * Ошибка если существует функтор, предикат БД или тип данных с таким же именем
 */
public class DuplicateError extends AbstractSemanticError {
    /**
     * Узел, который дублирует данный
     */
    private Node duplicate;

    public DuplicateError(String message, Node duplicate) {
        super(message);
        this.duplicate = duplicate;
    }

    public Node getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Node duplicate) {
        this.duplicate = duplicate;
    }
}
