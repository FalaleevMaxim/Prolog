package ru.prolog.runtime.context.rule;

import ru.prolog.etc.exceptions.runtime.ProgramInterruptedException;

/**
 * Перед и после вызова правила проверяет флаг {@link Thread#interrupted()} потока,
 * и если флаг {@code true}, прерывает выполнение программы исключением {@link ProgramInterruptedException}.
 * Это исключение обрабатывается в {@link ru.prolog.runtime.context.program.ExceptionsCatcherProgramContext}.
 */
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
