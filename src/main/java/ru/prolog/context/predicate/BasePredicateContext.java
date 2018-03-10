package ru.prolog.context.predicate;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.values.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasePredicateContext implements PredicateContext {
    private final Predicate predicate;
    private final List<Value> args;
    private final ProgramContext programContext;
    private Map<String, Object> contextData;
    private int startWith = 0;
    private RuleContext lastRuleContext;
    private boolean cut = false;

    public BasePredicateContext(Predicate predicate, List<Value> args){
        this(predicate, args, null);
    }

    public BasePredicateContext(Predicate predicate, List<Value> args, ProgramContext program) {
        if(predicate == null) throw new IllegalArgumentException("Can not create executable of null predicate");
        this.programContext = program;
        this.predicate = predicate;
        if(args == null) args = Collections.emptyList();
        this.args = Collections.unmodifiableList(args);
    }

    @Override
    public Predicate getPredicate() {
        return predicate;
    }

    @Override
    public List<Value> getArgs() {
        return args;
    }

    @Override
    public RuleContext getLastRuleContext() {
        return lastRuleContext;
    }

    @Override
    public void setLastRuleContext(RuleContext lastRuleContext) {
        this.lastRuleContext = lastRuleContext;
    }

    @Override
    public void putContextData(String key, Object data) {
        if(contextData==null) contextData = new HashMap<>();
        contextData.put(key, data);
    }

    @Override
    public Object getContextData(String key) {
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
    public ProgramContext programContext() {
        return programContext;
    }

    @Override
    public boolean execute(){
        if(cut) return false;
        int result = predicate.run(this, args, startWith);
        if(result < 0){
            return false;
        }else{
            startWith = result+1;
            return true;
        }
    }
}
