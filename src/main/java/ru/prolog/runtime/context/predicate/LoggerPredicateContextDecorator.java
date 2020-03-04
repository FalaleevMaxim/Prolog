package ru.prolog.runtime.context.predicate;

import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.util.keys.PredicateKeys;
import ru.prolog.util.ToStringUtil;
import ru.prolog.util.keys.ProgramKeys;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Выводит в файл лог вызовов предикатов.
 * Файл задаётся {@link #FILE_KEY}
 */
public class LoggerPredicateContextDecorator extends BasePredicateContextDecorator {
    /**
     * Ключ, по которому в контексте программы хранится уровень вызова.
     *
     * @see ProgramContext#getContextData(String)
     */
    private static final String LEVEL_KEY = ProgramKeys.LOG_LEVEL;

    /**
     * Ключ, по которому в контексте программы хранится уровень вызова.
     *
     * @see ProgramContext#getContextData(String)
     */
    private static final String FILE_KEY = ProgramKeys.LOG_FILE;

    /**
     * Уровень вызова. Перед обработкой вызова уровень берётся из контекста программы
     */
    private final int level;

    /**
     * Смещение перед выводом лога. Смещение состоит из {@link #level} повторений {@code "| "}.
     */
    private final String offset;

    /**
     * Оборачивает переданный вызов предиката для вывода лога в файл при вызове.
     * <p>
     * При оборачивании вызова, из контекста программы ({@link ProgramContext#getContextData(String)}) по ключу {@link #LEVEL_KEY} берётся {@link #level}.
     * Если уровень по ключу уже был задан, он увеличивается на 1; если нет, устанавливается в 0.
     * Значение уровня записывается обратно в контекст программы по тому же ключу, чтобы вложенные вызовы увеличивали у себя уровень вложенности.
     *
     * @param decorated декорируемый вызов предиката.
     */
    public LoggerPredicateContextDecorator(PredicateContext decorated) {
        super(decorated);

        Object level = programContext().getContextData(LEVEL_KEY);
        if (level == null) {
            this.level = 0;
        } else {
            this.level = (int) level + 1;
        }
        programContext().putContextData(LEVEL_KEY, this.level);
        offset = IntStream.range(0, this.level)
                .mapToObj(x -> "| ")
                .collect(Collectors.joining());
    }

    /**
     * Пишет в файл лог перед вызовом, вызывает {@link #decorated#execute()} у декорируемого объекта и выводит лог с результатами вызова.
     * <p>
     * Если предикат завершился с {@link PredicateResult#FAIL}, в контекст программы записывается уровень вызова на 1 меньше сохранённого в {@link #level}
     * Если предикат завершился удачно, в контекст записывается тот же уровень, который был сохранён.
     *
     * @return Результат вызова предиката, без изменений.
     */
    @Override
    public PredicateResult execute() {
        String fileName = (String) programContext().getContextData(FILE_KEY);
        if (fileName == null) return decorated.execute();
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
            pw.println(offset + "Execute predicate " + predicate());
            pw.println(offset + ToStringUtil.funcToString(predicate().getName(), getArgs()));
            programContext().putContextData(LEVEL_KEY, level);
        } catch (FileNotFoundException ignored) {
        }

        PredicateResult ret = decorated.execute();

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
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
