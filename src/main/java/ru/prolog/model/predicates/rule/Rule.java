package ru.prolog.model.predicates.rule;

import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.predicates.execution.rule.RuleExecution;
import ru.prolog.model.values.Value;
import ru.prolog.model.values.variables.Variable;

import java.util.Collections;
import java.util.List;

/**
 * Base class for all rules.
 * Implements method unifyArgs.
 * Can be used as fact (rule without body)
 */
public class Rule {
    private List<Value> toUnificateList;
    private Predicate predicate;

    public Rule(Predicate predicate, List<Value> toUnificateList) {
        if(toUnificateList == null) toUnificateList = Collections.emptyList();
        this.toUnificateList = toUnificateList;
        this.predicate = predicate;
    }

    public final boolean run(List<Value> args, RuleExecution context){
        if(!unifyArgs(args, context)) {
            return false;
        }
        return body(context);
    }

    protected boolean body(RuleExecution context) {
        return true;
    }

    /**
     * @param args Arguments sent to rule from parent context
     * @param context context of executing this rule
     * @return true if all arguments unification was successful
     */
    public final boolean unifyArgs(List<Value> args, RuleExecution context){
        for(int i = 0; i < toUnificateList.size(); i++ ){
            Value toUnificate = toUnificateList.get(i);
            toUnificate = processVariable(context, toUnificate);
            if(!toUnificate.unify(args.get(i))) return false;
        }
        return true;
    }

    /**
     * If arg is variable, returns variable from context
     * @param context Context to get variables
     * @param arg argument sent to predicate or to unificate in rule.
     * @return Same arg if it is not variable or variable from context.
     */
    protected Value processVariable(RuleExecution context, Value arg) {
        if(arg instanceof Variable){
            arg = context.getVariable(((Variable)arg).getName(), arg.getType());
        }
        return arg;
    }

    public Predicate getPredicate() {
        return predicate;
    }
}
