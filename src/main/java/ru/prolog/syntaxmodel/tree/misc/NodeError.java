package ru.prolog.syntaxmodel.tree.misc;

import ru.prolog.syntaxmodel.tree.Node;

public class NodeError {
    /**
     * Узел, с которым ассоциирована ошибка
     */
    private final Node child;
    /**
     * Ошибка после указанного узла или непосредственно на нём
     */
    private final boolean after;
    /**
     * Текст ошибки
     */
    private final String text;

    public NodeError(Node child, String text) {
        this.child = child;
        this.after = false;
        this.text = text;
    }

    public NodeError(Node child, boolean after, String text) {
        this.child = child;
        this.after = after;
        this.text = text;
    }

    public Node getChild() {
        return child;
    }

    public boolean isAfter() {
        return after;
    }

    public String getText() {
        return text;
    }
}
