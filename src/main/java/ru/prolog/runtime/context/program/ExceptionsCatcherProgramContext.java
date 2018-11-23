package ru.prolog.runtime.context.program;

import ru.prolog.etc.exceptions.runtime.ProgramInterruptedException;
import ru.prolog.etc.exceptions.runtime.PrologRuntimeException;
import ru.prolog.util.io.ErrorListener;

/**
 * Декоратор контекста программы, перехватывающий исключения, выпавшие при выполнении программы.
 * Информация об исключениях выводится на устройства вывода ошибок программы.
 * <p>
 * Исключения, которые предикаты бросают специально для завершения программы с ошибкой - {@link PrologRuntimeException}
 * - выводятся с помощью {@link ErrorListener#prologRuntimeException(PrologRuntimeException)},
 * после чего выводится строка "Program finished with Prolog runtime exception"
 * <p>
 * Исключение {@link ProgramInterruptedException} выбрасывается
 * {@link ru.prolog.runtime.context.predicate.TerminatingPredicateContext} и {@link ru.prolog.runtime.context.rule.TerminatingRuleContext}
 * если поток программы был остановлен флагом {@link Thread#interrupted()}.
 * В таком случае выводится строка "Program terminated" и сообщение исключения, если оно есть.
 * <p>
 * Все остальные исключения выводятся с помощью {@link ErrorListener#runtimeException(RuntimeException)}
 */
public class ExceptionsCatcherProgramContext extends BaseProgramContextDecorator {

    public ExceptionsCatcherProgramContext(ProgramContext decorated) {
        super(decorated);
    }

    @Override
    public boolean execute() {
        try {
            return decorated.execute();
        } catch (PrologRuntimeException e) {
            ErrorListener errorListener = getErrorListeners();
            errorListener.prologRuntimeException(e);
            errorListener.println("Program finished with Prolog runtime exception");
            return false;
        } catch (ProgramInterruptedException e) {
            ErrorListener errorListener = getErrorListeners();
            errorListener.print("Program terminated");
            if (e.getMessage() == null) errorListener.println(": " + e.getMessage());
            else errorListener.print("\n");
            return false;
        } catch (RuntimeException e) {
            ErrorListener errorListener = getErrorListeners();
            errorListener.runtimeException(e);
            errorListener.println("Program finished with Java runtime exception");
            return false;
        }
    }
}
