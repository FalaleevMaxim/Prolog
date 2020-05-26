package ru.prolog.syntaxmodel.tree.semantics.attributes;

public class ToVariablesHolder extends AbstractSemanticAttribute {
    private final VariablesHolder variablesHolder;

    public ToVariablesHolder(VariablesHolder variablesHolder) {
        this.variablesHolder = variablesHolder;
    }

    public VariablesHolder getVariablesHolder() {
        return variablesHolder;
    }
}
