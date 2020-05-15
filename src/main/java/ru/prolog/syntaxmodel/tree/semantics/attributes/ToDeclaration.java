package ru.prolog.syntaxmodel.tree.semantics.attributes;

import ru.prolog.syntaxmodel.tree.Node;

/**
 * Связь к объявлению
 */
public class ToDeclaration extends AbstractSemanticAttribute {
    private Node declaration;

    public ToDeclaration(Node declaration) {
        this.declaration = declaration;
    }

    public Node getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Node declaration) {
        this.declaration = declaration;
    }
}
