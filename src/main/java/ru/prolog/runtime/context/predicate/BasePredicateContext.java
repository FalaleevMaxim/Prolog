package ru.prolog.runtime.context.predicate;

import ru.prolog.model.ModelObject;
import ru.prolog.model.managers.rule.RuleContextManager;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.rule.Statement;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.util.ToStringUtil;
import ru.prolog.util.io.ErrorListenerHub;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasePredicateContext implements PredicateContext {
    /**
     * Выполняемый предикат. Не может быть null.
     *
     * @see #predicate()
     */
    private final Predicate predicate;

    /**
     * Выражение, которое выполняется. Может быть null, если предикат - цель.
     * @see #statement()
     */
    private final Statement statement;

    /**
     * Аргументы, передаваемые предикату при вызове
     * @see #getArgs()
     */
    private final List<Value> args;

    /**
     * Ссылка на контекст программы.
     * @see #programContext()
     */
    private final ProgramContext programContext;

    /**
     * Контекст правила, вызвавшего предикат. Может быть null, если это предикат-цель.
     * @see #ruleContext()
     */
    private RuleContext ruleContext;

    /**
     * Данные, которые предикат сохраняет в контексте вызова для дальнейшего использования при бэктрекинге.
     * Например, номер и контекст последнего выполненного правила.
     * @see #putContextData(String, Object)
     * @see #getContextData(String)
     */
    private Map<String, Object> contextData;

    /**
     * Флаг, показывающий, было ли отсечение в предикате.
     * Требуется только для обычных предикатов, работающих с правилами.
     * Если в предикате было отсеччение, перебор правил не продолжается.
     */
    private boolean cut = false;

    /**
     * Последний результат, возвращённый при выполнении предиката.
     * Если это не {@link PredicateResult#NEXT_RESULT} (а точнее, если {@link PredicateResult#canRunAgain()}) возвращает {@code false},
     *  вызов {@link #execute()} возвращает {@link PredicateResult#FAIL}.
     */
    private PredicateResult lastResult = PredicateResult.NEXT_RESULT;

    public BasePredicateContext(Statement statement, List<Value> args, RuleContext ruleContext) {
        this.statement = statement;
        this.predicate = statement.getPredicate();
        this.ruleContext = ruleContext;
        this.programContext = ruleContext.programContext();
        this.args = args;
    }

    public BasePredicateContext(Predicate predicate, List<Value> args, ProgramContext program) {
        if (predicate == null) throw new IllegalArgumentException("Can not create context of null predicate");
        this.programContext = program;
        this.predicate = predicate;
        this.statement = null;
        if (args == null) args = Collections.emptyList();
        this.args = Collections.unmodifiableList(args);
    }

    @Override
    public Predicate predicate() {
        return predicate;
    }

    @Override
    public List<Value> getArgs() {
        return args;
    }

    @Override
    public RuleContextManager getRuleManager() {
        return programContext.program().managers().getRuleManager();
    }

    @Override
    public RuleContext ruleContext() {
        return ruleContext;
    }

    @Override
    public Statement statement() {
        return statement;
    }

    @Override
    public ProgramContext programContext() {
        return programContext;
    }

    @Override
    public void putContextData(String key, Object data) {
        if (contextData == null) contextData = new HashMap<>();
        contextData.put(key, data);
    }

    @Override
    public Object getContextData(String key) {
        if (contextData == null) return null;
        return contextData.get(key);
    }

    @Override
    public void cut() {
        cut = true;
    }

    @Override
    public boolean isCut() {
        return cut;
    }

    @Override
    public boolean failed() {
        return lastResult == PredicateResult.FAIL;
    }

    /**
     * Вызывает предикат и возвращает результат выполнения. Результат выполнения предиката записывается в {@link #lastResult}.
     * Не выполняет предикат и возвращает {@link PredicateResult#FAIL}, если после прошлого вызова нет смысла запускать предикат заново.
     * @see PredicateResult#canRunAgain()
     * @return результат вызова предиката, или {@link PredicateResult#FAIL}, если запускать предикат больше нет смысла.
     */
    @Override
    public PredicateResult execute() {
        //Если последний запуск завершился fail или вернул окончательный результат, повторно выполнять не требуется.
        if (!lastResult.canRunAgain()) return PredicateResult.FAIL;
        //Запуск предиката и сохранение результата.
        lastResult = predicate().run(this, args);
        //Если предикат завершился неудачей, выполнить соответствующие действия по очистке контекста
        if (lastResult.fail()) {
            onFail();
        }
        return lastResult;
    }

    /**
     * Чистит данные контекста после неуспешного выполнения предиката.
     * @see #clearContextData()
     */
    private void onFail() {
        if (contextData != null) {
            clearContextData();
        }
    }

    /**
     * Очищает сохранённые данные в контексте.
     * Если сохранённый объект реализует {@link AutoCloseable}, он закрывается перед удалением.
     * Ошибки при закрытии объекта выводятся на вывод ошибок программы.
     * @see #contextData
     * @see #putContextData(String, Object)
     * @see #getContextData(String)
     */
    private void clearContextData() {
        for (Object o : contextData.values()) {
            if (o instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) o).close();
                } catch (Exception e) {
                    ErrorListenerHub errorListeners = programContext().getErrorListeners();
                    errorListeners.println("Error closing predicate context resource:");
                    errorListeners.println(e.toString() + '\n');
                }
            }
        }
        contextData.clear();
    }

    /**
     * Если вызов произведён от выражения, моделью является выражение.
     * Если это вызов предиката-цели, моделью является предикат.
     * @return выражение или предикат-цель, который исполняется в контексте.
     */
    @Override
    public ModelObject model() {
        if (statement != null) {
            return statement;
        } else {
            return predicate;
        }
    }

    @Override
    public String toString() {
        if (args.isEmpty()) {
            return "Call to predicate " + predicate;
        } else {
            return "Predicate " + predicate + " called as " + ToStringUtil.funcToString(predicate().getName(), args);
        }
    }
}
