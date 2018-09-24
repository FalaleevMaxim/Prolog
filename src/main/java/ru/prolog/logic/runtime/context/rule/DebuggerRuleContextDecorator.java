package ru.prolog.logic.runtime.context.rule;

import ru.prolog.logic.runtime.context.program.ProgramContext;
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
            pw.println(offset + "Redo rule " + rule());
            pw.println(offset + ToStringUtil.funcToString(rule().getPredicate().getName(), getArgs()));
            pw.close();
            programContext().putContextData(LEVEL_KEY, level);
        } catch (FileNotFoundException ignored) {
        }

        boolean ret = decorated.redo();

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
            if (ret) {
                pw.println(offset + "Return from redo " + rule());
                pw.println(offset + ToStringUtil.funcToString(rule().getPredicate().getName(), getArgs()));
                programContext().putContextData(LEVEL_KEY, level);
                return true;
            } else {
                pw.println(offset + "Redo failed. Return from " + rule());
                pw.println(offset + ToStringUtil.funcToString(rule().getPredicate().getName(), getArgs()));
                programContext().putContextData(LEVEL_KEY, level - 1);
                return false;
            }
        } catch (FileNotFoundException ignored) {
        }

        return ret;
    }

    @Override
    public boolean execute() {
        String fileName = (String) programContext().getContextData(ProgramContext.KEY_DEBUG_FILE);
        if(fileName==null) return decorated.execute();
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
            pw.println(offset + "Execute rule " + rule());
            pw.println(offset + ToStringUtil.funcToString(rule().getPredicate().getName(), getArgs()));
            programContext().putContextData(LEVEL_KEY, level);
        } catch (FileNotFoundException ignored) {
        }

        boolean ret = decorated.execute();

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
            if (ret) {
                pw.println(offset + "Return from " + rule());
                pw.println(offset + ToStringUtil.funcToString(rule().getPredicate().getName(), getArgs()));
                programContext().putContextData(LEVEL_KEY, level);
                return true;
            } else {
                pw.println(offset + "Rule failed. Return from " + rule());
                pw.println(offset + ToStringUtil.funcToString(rule().getPredicate().getName(), getArgs()));
                programContext().putContextData(LEVEL_KEY, level - 1);
                return false;
            }
        } catch (FileNotFoundException ignored) {
        }

        return ret;
    }
}
