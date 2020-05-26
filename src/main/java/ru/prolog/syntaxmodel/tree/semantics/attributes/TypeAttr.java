package ru.prolog.syntaxmodel.tree.semantics.attributes;

import ru.prolog.syntaxmodel.tree.nodes.TypeDefNode;

public class TypeAttr {
    /**
     * Имя примитивного типа
     */
    private String primitiveName;

    /**
     * Ссылка на объявление типа
     */
    private TypeDefNode typeDef;

    public String getPrimitiveName() {
        return primitiveName;
    }

    public void setPrimitiveName(String primitiveName) {
        this.primitiveName = primitiveName;
    }

    public TypeDefNode getTypeDef() {
        return typeDef;
    }

    public void setTypeDef(TypeDefNode typeDef) {
        this.typeDef = typeDef;
    }
}
