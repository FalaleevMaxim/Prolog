package ru.prolog.logic.context.rule;

import ru.prolog.util.ToStringUtil;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DebuggerRuleContextDecorator extends BaseRuleContextDecorator {
    public static final String LEVEL_KEY = "Debug.level";
    private final int level;
    private final String offset;

    public DebuggerRuleContextDecorator(RuleContext decorated) {
        super(decorated);
        Object level = programContext().getContextData(LEVEL_KEY);
        if(level==null) {
            this.level = 0;
        }else {
            this.level = (int) level + 1;
        }
        programContext().putContextData(LEVEL_KEY, this.level);
        offset = IntStream.range(0, this.level)
                .mapToObj(x -> "| ")
                .collect(Collectors.joining());
    }

    @Override
    public boolean redo() {
        System.out.println(offset + "Redo rule "+getRule());
        System.out.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
        programContext().putContextData(LEVEL_KEY, level);
        boolean ret = super.redo();
        //programContext().putContextData(LEVEL_KEY, level);
        if(ret) {
            System.out.println(offset + "Return from redo " + getRule());
            System.out.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
            programContext().putContextData(LEVEL_KEY, level);
            return true;
        }else{
            System.out.println(offset + "Redo failed. Return from " + getRule());
            System.out.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
            programContext().putContextData(LEVEL_KEY, level-1);
            return false;
        }
    }

    @Override
    public boolean execute() {
        System.out.println(offset + "Execute rule "+getRule());
        System.out.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
        programContext().putContextData(LEVEL_KEY, level);
        boolean ret = decorated.execute();
        //programContext().putContextData(LEVEL_KEY, level);
        if(ret) {
            System.out.println(offset + "Return from " + getRule());
            System.out.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
            programContext().putContextData(LEVEL_KEY, level);
            return true;
        }else{
            System.out.println(offset + "Rule failed. Return from " + getRule());
            System.out.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
            programContext().putContextData(LEVEL_KEY, level-1);
            return false;
        }
    }
}
