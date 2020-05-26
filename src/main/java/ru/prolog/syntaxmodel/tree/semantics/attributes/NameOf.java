package ru.prolog.syntaxmodel.tree.semantics.attributes;

import ru.prolog.syntaxmodel.tree.Node;

/**
 * Атрибут, указывающий что данный токен является именем
 */
public class NameOf extends AbstractSemanticAttribute {
    /**
     * Узел, именем которого является данный токен
     */
    private Node namedNode;

    public NameOf(Node namedNode) {
        this.namedNode = namedNode;
    }

    public Node getNamedNode() {
        return namedNode;
    }

    public void setNamedNode(Node namedNode) {
        this.namedNode = namedNode;
    }

    public String getName() {
        return getSelfNode().getText();
    }
}
