package ru.prolog.syntaxmodel.tree.semantics.attributes;

import java.util.HashSet;
import java.util.Set;

/**
 * Хранит переменные из левой части правила
 */
public class RuleLeftVariablesHolder extends VariablesHolder {
    private Set<StatementSetVariablesHolder> statementSetVariablesHolders = new HashSet<>();

    public Set<StatementSetVariablesHolder> getStatementSetVariablesHolders() {
        return statementSetVariablesHolders;
    }

    public void setStatementSetVariablesHolders(Set<StatementSetVariablesHolder> statementSetVariablesHolders) {
        this.statementSetVariablesHolders = statementSetVariablesHolders;
    }

    public void addStatementSetVariablesHolder(StatementSetVariablesHolder statementSetVariablesHolder) {
        statementSetVariablesHolders.add(statementSetVariablesHolder);
    }
}
