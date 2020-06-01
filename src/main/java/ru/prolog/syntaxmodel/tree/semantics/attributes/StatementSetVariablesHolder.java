package ru.prolog.syntaxmodel.tree.semantics.attributes;

public class StatementSetVariablesHolder extends VariablesHolder {
    private RuleLeftVariablesHolder ruleLeftVariablesHolder;

    public StatementSetVariablesHolder(RuleLeftVariablesHolder ruleLeftVariablesHolder) {
        setRuleLeftVariablesHolder(ruleLeftVariablesHolder);
    }

    public StatementSetVariablesHolder() {
    }

    public RuleLeftVariablesHolder getRuleLeftVariablesHolder() {
        return ruleLeftVariablesHolder;
    }

    public void setRuleLeftVariablesHolder(RuleLeftVariablesHolder ruleLeftVariablesHolder) {
        if(ruleLeftVariablesHolder != null) {
            this.ruleLeftVariablesHolder = ruleLeftVariablesHolder;
            ruleLeftVariablesHolder.addStatementSetVariablesHolder(this);
        }
    }
}
