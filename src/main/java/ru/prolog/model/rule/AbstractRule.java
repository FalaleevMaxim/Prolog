package ru.prolog.model.rule;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all rules.
 * Implements method unifyArgs.
 */
public abstract class AbstractRule implements Rule {
    protected List<Value> toUnifyList;
    protected Predicate predicate;

    public AbstractRule(Predicate predicate, List<Value> toUnifyList) {
        if(toUnifyList == null) toUnifyList = Collections.emptyList();
        this.toUnifyList = Collections.unmodifiableList(new ArrayList<>(toUnifyList));
        this.predicate = predicate;
    }

    protected AbstractRule(List<Value> toUnifyList){
        if(toUnifyList == null) toUnifyList = Collections.emptyList();
        this.toUnifyList = Collections.unmodifiableList(new ArrayList<>(toUnifyList));
    }

    public List<Value> getUnifyArgs(){
        return toUnifyList;
    }

    public final boolean run(List<Value> args, RuleContext context){
        if(!unifyArgs(args, context)) {
            return false;
        }
        return body(context);
    }

    public boolean body(RuleContext context) {
        return true;
    }

    /**
     * @param args Arguments sent to rule from parent context
     * @param context context of executing this rule
     * @return true if all arguments unification was successful
     */
    public final boolean unifyArgs(List<Value> args, RuleContext context){
        for(int i = 0; i < toUnifyList.size(); i++ ){
            Value toUnify = toUnifyList.get(i);
            toUnify = toUnify.forContext(context);
            if(!toUnify.unify(args.get(i))) return false;
        }
        return true;
    }

    public Predicate getPredicate() {
        return predicate;
    }
}
