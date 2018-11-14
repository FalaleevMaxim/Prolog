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
    private final Predicate predicate;
    private final Statement statement;
    private final List<Value> args;
    private final ProgramContext programContext;
    private RuleContext ruleContext;
    private Map<String, Object> contextData;
    private boolean cut = false;
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

    @Override
    public PredicateResult execute() {
        //При отсечении внутри предиката поиск не продолжается
        if (cut) return PredicateResult.FAIL;
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

    private void onFail() {
        if (contextData != null) {
            clearContextData();
        }
    }

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
        if (statement == null) {
            return "Call to predicate " + predicate;
        } else {
            return "Predicate " + predicate + " called as " + ToStringUtil.funcToString(predicate().getName(), args);
        }
    }
}
