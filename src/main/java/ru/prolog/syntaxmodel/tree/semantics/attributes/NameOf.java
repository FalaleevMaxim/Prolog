package ru.prolog.syntaxmodel.tree.semantics.attributes;

import ru.prolog.syntaxmodel.tree.interfaces.Named;

/**
 * Атрибут, указывающий что данный токен является именем
 */
public class NameOf extends AbstractSemanticAttribute {
    /**
     * Узел, именем которого является данный токен
     */
    private Named namedNode;

    public NameOf(Named namedNode) {
        this.namedNode = namedNode;
    }

    public Named getNamedNode() {
        return namedNode;
    }

    public void setNamedNode(Named namedNode) {
        this.namedNode = namedNode;
    }

    public String getName() {
        return getSelfNode().getText();
    }
}
