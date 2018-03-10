package ru.prolog.model.predicates.rule;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.Value;

import java.util.Collections;
import java.util.List;

/**
 * Base class for all rules.
 * Implements method unifyArgs.
 */
public abstract class Rule {
    private List<Value> toUnificateList;
    private Predicate predicate;

    public Rule(Predicate predicate, List<Value> toUnificateList) {
        if(toUnificateList == null) toUnificateList = Collections.emptyList();
        this.toUnificateList = toUnificateList;
        this.predicate = predicate;
    }

    public final boolean run(List<Value> args, RuleContext context){
        if(!unifyArgs(args, context)) {
            return false;
        }
        return body(context);
    }

    protected boolean body(RuleContext context) {
        return true;
    }

    /**
     * @param args Arguments sent to rule from parent context
     * @param context context of executing this rule
     * @return true if all arguments unification was successful
     */
    public final boolean unifyArgs(List<Value> args, RuleContext context){
        for(int i = 0; i < toUnificateList.size(); i++ ){
            Value toUnificate = toUnificateList.get(i);
            toUnificate = toUnificate.forContext(context);
            if(!toUnificate.unify(args.get(i))) return false;
        }
        return true;
    }

    public Predicate getPredicate() {
        return predicate;
    }
}
