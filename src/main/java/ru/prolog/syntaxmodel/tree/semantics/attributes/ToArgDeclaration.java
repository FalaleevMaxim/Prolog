package ru.prolog.syntaxmodel.tree.semantics.attributes;

import ru.prolog.syntaxmodel.tree.nodes.TypeNameNode;

/**
 * К объявлению параметра предиката или функтора
 */
public class ToArgDeclaration extends AbstractSemanticAttribute {
    /**
     * Номер параметра
     */
    int argNum;

    /**
     * Имя встроенного предиката
     */
    private String builtInPredicateName;

    /**
     * Объявление параметра
     */
    private TypeNameNode argDeclaration;

    /**
     * Данный узел является параметром вызова встроенного предиката, если нет ссылки на объявления параметра
     */
    public boolean isBuiltIn() {
        return argDeclaration == null;
    }

    public int getArgNum() {
        return argNum;
    }

    public void setArgNum(int argNum) {
        this.argNum = argNum;
    }

    public String getBuiltInPredicateName() {
        return builtInPredicateName;
    }

    public void setBuiltInPredicateName(String builtInPredicateName) {
        this.builtInPredicateName = builtInPredicateName;
    }

    public TypeNameNode getArgDeclaration() {
        return argDeclaration;
    }

    public void setArgDeclaration(TypeNameNode argDeclaration) {
        this.argDeclaration = argDeclaration;
    }
}
