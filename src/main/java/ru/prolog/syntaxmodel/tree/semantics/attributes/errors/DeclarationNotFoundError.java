package ru.prolog.syntaxmodel.tree.semantics.attributes.errors;

import ru.prolog.syntaxmodel.tree.nodes.FunctorDefNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Не найдено объявление используемого предиката, функтора или типа данных
 */
public class DeclarationNotFoundError extends AbstractSemanticError {
    private List<FunctorDefNode> possibleDeclarations;

    public DeclarationNotFoundError(String message) {
        super(message);
    }

    public DeclarationNotFoundError(String message, List<FunctorDefNode> possibleDeclarations) {
        super(message);
        this.possibleDeclarations = new ArrayList<>(possibleDeclarations);
    }

    public List<FunctorDefNode> getPossibleDeclarations() {
        return possibleDeclarations;
    }

    public void setPossibleDeclarations(List<FunctorDefNode> possibleDeclarations) {
        this.possibleDeclarations = possibleDeclarations;
    }
}
