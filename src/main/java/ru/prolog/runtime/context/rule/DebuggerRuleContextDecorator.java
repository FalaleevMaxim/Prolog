package ru.prolog.runtime.context.rule;

import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.util.ToStringUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Выводит в файл лог вызовов правил.
 * Файл задаётся {@link ProgramContext#KEY_DEBUG_FILE}
 */
public class DebuggerRuleContextDecorator extends BaseRuleContextDecorator {
    /**
     * Ключ, по которому в контексте программы хранится уровень вызова.
     *
     * @see ProgramContext#getContextData(String)
     */
    public static final String LEVEL_KEY = "Debug.level";

    /**
     * Уровень вызова. Перед обработкой вызова уровень берётся из контекста программы.
     */
    private final int level;

    /**
     * Смещение перед выводом лога. Смещение состоит из {@link #level} повторений {@code "| "}.
     */
    private final String offset;

    /**
     * Оборачивает переданный вызов правила для вывода лога в файл при вызове.
     * <p>
     * При оборачивании вызова, из контекста программы ({@link ProgramContext#getContextData(String)}) по ключу {@link #LEVEL_KEY} берётся {@link #level}.
     * Если уровень по ключу уже был задан, он увеличивается на 1; если нет, устанавливается в 0.
     * Значение уровня записывается обратно в контекст программы по тому же ключу, чтобы вложенные вызовы увеличивали у себя уровень вложенности.
     *
     * @param decorated декорируемый вызов правила.
     */
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

    /**
     * Работает аналогично {@link #execute()}, но вместо "Execute" в лог пишется "Redo".
     *
     * @return Результат повторного вызова правила, без изменений.
     */
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

    /**
     * Пишет в файл лог перед вызовом, вызывает {@link #decorated#execute()} у декорируемого объекта и выводит лог с результатами вызова.
     * <p>
     * Если правило завершилось с {@code false}, в контекст программы записывается уровень вызова на 1 меньше сохранённого в {@link #level}
     * Если правило завершилось удачно, в контекст записывается тот же уровень, который был сохранён.
     *
     * @return Результат вызова правила, без изменений.
     */
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
