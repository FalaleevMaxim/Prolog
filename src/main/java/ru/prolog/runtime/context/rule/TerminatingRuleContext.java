package ru.prolog.runtime.context.rule;

import ru.prolog.etc.exceptions.runtime.ProgramInterruptedException;

public class TerminatingRuleContext extends BaseRuleContextDecorator {
    public TerminatingRuleContext(RuleContext decorated) {
        super(decorated);
    }

    @Override
    public boolean execute() {
        checkInterrupted();
        if(!decorated.execute()) return false;
        checkInterrupted();
        return true;
    }

    @Override
    public boolean redo() {
        checkInterrupted();
        if(!decorated.redo()) return false;
        checkInterrupted();
        return true;
    }

    private void checkInterrupted() {
        if(Thread.interrupted()){
            throw new ProgramInterruptedException();
        }
    }
}
