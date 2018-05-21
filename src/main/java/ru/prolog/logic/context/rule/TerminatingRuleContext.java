package ru.prolog.logic.context.rule;

public class TerminatingRuleContext extends BaseRuleContextDecorator {
    public TerminatingRuleContext(RuleContext decorated) {
        super(decorated);
    }

    @Override
    public boolean execute() {
        if(Thread.interrupted()){
            Thread.currentThread().interrupt();
            return false;
        }
        if(!decorated.execute()) return false;
        if(Thread.interrupted()){
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }
}
