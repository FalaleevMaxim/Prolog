package ru.prolog.logic.context.predicate;

import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.managers.rule.RuleContextManager;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.values.Value;
import ru.prolog.util.io.ErrorListenerHub;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasePredicateContext implements PredicateContext {
    private final Predicate predicate;
    private final List<Value> args;
    private final ProgramContext programContext;
    private RuleContext ruleContext;
    private Map<String, Object> contextData;
    private int startWith = 0;
    private RuleContext lastExecutedRuleContext;
    private boolean cut = false;

    public BasePredicateContext(Predicate predicate, List<Value> args, RuleContext ruleContext) {
        this(predicate, args, ruleContext.programContext());
        this.ruleContext = ruleContext;
    }

    public BasePredicateContext(Predicate predicate, List<Value> args, ProgramContext program) {
        if (predicate == null) throw new IllegalArgumentException("Can not create context of null predicate");
        this.programContext = program;
        this.predicate = predicate;
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
    public RuleContext getRuleContext() {
        return ruleContext;
    }

    @Override
    public ProgramContext programContext() {
        return programContext;
    }

    @Override
    public RuleContext getLastExecutedRuleContext() {
        return lastExecutedRuleContext;
    }

    @Override
    public void setLastExecutedRuleContext(RuleContext lastExecutedRuleContext) {
        this.lastExecutedRuleContext = lastExecutedRuleContext;
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
    public boolean execute() {
        if (cut) return false;
        int result = predicate.run(this, args, startWith);
        if (result < 0) {
            onFail();
            return false;
        } else {
            startWith = result + 1;
            return true;
        }
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
}
