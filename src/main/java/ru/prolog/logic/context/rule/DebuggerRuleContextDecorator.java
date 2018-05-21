package ru.prolog.logic.context.rule;

import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.util.ToStringUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
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
        String fileName = (String) programContext().getContextData(ProgramContext.KEY_DEBUG_FILE);
        if(fileName==null) return decorated.execute();
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
            pw.println(offset + "Redo rule " + getRule());
            pw.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
            pw.close();
            programContext().putContextData(LEVEL_KEY, level);
        } catch (FileNotFoundException e) { }

        boolean ret = super.redo();

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
            if (ret) {
                pw.println(offset + "Return from redo " + getRule());
                pw.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
                programContext().putContextData(LEVEL_KEY, level);
                return true;
            } else {
                pw.println(offset + "Redo failed. Return from " + getRule());
                pw.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
                programContext().putContextData(LEVEL_KEY, level - 1);
                return false;
            }
        } catch (FileNotFoundException e) { }

        return ret;
    }

    @Override
    public boolean execute() {
        String fileName = (String) programContext().getContextData(ProgramContext.KEY_DEBUG_FILE);
        if(fileName==null) return decorated.execute();
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
            pw.println(offset + "Execute rule " + getRule());
            pw.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
            programContext().putContextData(LEVEL_KEY, level);
        } catch (FileNotFoundException e) { }

        boolean ret = decorated.execute();

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
            if (ret) {
                pw.println(offset + "Return from " + getRule());
                pw.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
                programContext().putContextData(LEVEL_KEY, level);
                return true;
            } else {
                pw.println(offset + "Rule failed. Return from " + getRule());
                pw.println(offset + ToStringUtil.funcToString(getRule().getPredicate().getName(), getArgs()));
                programContext().putContextData(LEVEL_KEY, level - 1);
                return false;
            }
        } catch (FileNotFoundException e) { }

        return ret;
    }
}
