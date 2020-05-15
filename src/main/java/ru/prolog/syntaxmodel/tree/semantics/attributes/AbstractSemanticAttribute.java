package ru.prolog.syntaxmodel.tree.semantics.attributes;

import ru.prolog.syntaxmodel.tree.Node;

public abstract class AbstractSemanticAttribute {
    private Node selfNode;

    public Node getSelfNode() {
        if(selfNode == null) {
            throw new IllegalStateException("Self node not set");
        }
        return selfNode;
    }

    public void setSelfNode(Node selfNode) {
        if(this.selfNode != null) {
            throw new IllegalStateException("Node already set");
        }
        this.selfNode = selfNode;
    }
}
