package ru.prolog.runtime.context.predicate;

import ru.prolog.model.ModelObject;
import ru.prolog.model.managers.rule.RuleContextManager;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.program.Program;
import ru.prolog.model.rule.Statement;
import ru.prolog.runtime.RuntimeObject;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

/**
 * Контекст вызова предиката. При каждом вызове создаётся объект контекста.
 */
public interface PredicateContext extends RuntimeObject {
    /**
     * Возвращает контекст программы, в рамках которого происходит данный вызов
     *
     * @return контекст программы
     */
    ProgramContext programContext();

    /**
     * Возвращает контекст вызова правила, из которого произведён данный вызов.
     *
     * @return Контекст вызова правила, из которого вызван предикат.
     */
    RuleContext ruleContext();

    /**
     * Возвращает выражение, которое было вызвано. Может быть {@code null}, если это вызов предиката-цели.
     *
     * @return Выражение, из которого был произведён этот вызов.
     */
    Statement statement();

    /**
     * Возвращает предикат, выполняемый в этом вызове. Не может быть {@code null}
     *
     * @return Предикат, выполняемый в этом вызове.
     */
    Predicate predicate();

    /**
     * Выполняет предикат.
     *
     * @return Результат вызова предиката.
     */
    PredicateResult execute();

    @Override
    default ModelObject model() {
        return statement() != null ? statement() : predicate();
    }

    /**
     * Возвращает список аргументов, которые следует передать предикату при вызове.
     *
     * @return Список аргументов, которые следует передать предикату при вызове.
     */
    List<Value> getArgs();

    /**
     * Метод, упрощающий доступ к менеджеру правил.
     *
     * @return менеджер правил, хранящийся в объекте программы ({@link Program#managers()})
     */
    RuleContextManager getRuleManager();

    /**
     * Сохраняет данные по ключу в контексте вызова предиката. Получить данные сможет только предикат из того же вызова.
     * Предназначен для хранения данных в контексте вызова на случай бэктрекинга к этому же вызову.
     * Сохранённые данные будут удалены при выходе из контекста вызова (если предикат вернёт {@link PredicateResult#FAIL} или {@link PredicateResult#LAST_RESULT}).
     * Ключи, используемые встроенными функциями интерпретатора и языка, перечислены в {@link ru.prolog.util.keys.PredicateKeys}
     *
     * @param key  Ключ, по которому будет сохранён объект.
     * @param data Сохраняемый объект
     * @see #getContextData(String)
     */
    void putContextData(String key, Object data);

    /**
     * Возвращает данные, сохранённые в контексте по ключу.
     * Ключи, используемые встроенными функциями интерпретатора и языка, перечислены в {@link ru.prolog.util.keys.PredicateKeys}
     *
     * @param key ключ, по которому объект был сохранён в методе {@link #putContextData(String, Object) putContextData}.
     * @return сохранённый по ключу объект или {@code null}.
     */
    Object getContextData(String key);

    /**
     * Указывает, что в предикате было отсечение, и продолжать перебор правил не нужно.
     * Используется только в предикатах, использующих правила.
     */
    void cut();

    /**
     * Показывает, встречалось ли в выполняемом предикатом правиле отсечение.
     *
     * @return возвращает {@code true}, если метод {@link #cut() cut()} был вызван хотя бы один раз; иначе возвращает {@code false}.
     */
    boolean isCut();

    /**
     * Возвращает true, если предикат завершился неудачно.
     *
     * @return Возвращает {@code true}, если предикат вернул {@link ru.prolog.model.predicate.PredicateResult#FAIL fail}, и контекст больше нельзя использовать
     */
    boolean failed();
}
