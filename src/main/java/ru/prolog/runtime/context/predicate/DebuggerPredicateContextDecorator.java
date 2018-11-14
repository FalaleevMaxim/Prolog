package ru.prolog.runtime.context.predicate;

import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.DebuggerRuleContextDecorator;
import ru.prolog.util.ToStringUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DebuggerPredicateContextDecorator extends BasePredicateContextDecorator{
    public static final String LEVEL_KEY = DebuggerRuleContextDecorator.LEVEL_KEY;
    private final int level;
    private final String offset;

    public DebuggerPredicateContextDecorator(PredicateContext decorated) {
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
    public PredicateResult execute() {
        String fileName = (String) programContext().getContextData(ProgramContext.KEY_DEBUG_FILE);
        if(fileName==null) return decorated.execute();
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))){
            pw.println(offset + "Execute predicate "+predicate());
            pw.println(offset + ToStringUtil.funcToString(predicate().getName(), getArgs()));
            programContext().putContextData(LEVEL_KEY, level);
        } catch (FileNotFoundException ignored) {
        }

        PredicateResult ret = decorated.execute();

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))){
            switch (ret) {
                case NEXT_RESULT:
                    pw.println(offset + "Return from predicate " + predicate());
                    pw.println(offset + ToStringUtil.funcToString(predicate().getName(), getArgs()));
                    programContext().putContextData(LEVEL_KEY, level);
                    break;
                case LAST_RESULT:
                    pw.println(offset + "Return last result from predicate " + predicate());
                    pw.println(offset + ToStringUtil.funcToString(predicate().getName(), getArgs()));
                    programContext().putContextData(LEVEL_KEY, level);
                    break;
                case FAIL:
                    pw.println(offset + "Predicate failed. Return from " + predicate());
                    pw.println(offset + ToStringUtil.funcToString(predicate().getName(), getArgs()));
                    programContext().putContextData(LEVEL_KEY, level - 1);
                    break;
            }
            return ret;
        } catch (FileNotFoundException ignored) {
        }
        return ret;
    }
}
