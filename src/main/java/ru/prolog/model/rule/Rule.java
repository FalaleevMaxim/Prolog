package ru.prolog.model.rule;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.ModelBuilder;
import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.values.Value;

import java.util.List;

public interface Rule extends ModelObject {
    Predicate getPredicate();
    List<Value> getUnifyArgs();
    boolean run(List<Value> args, RuleContext context);
    boolean body(RuleContext context);
    boolean unifyArgs(List<Value> args, RuleContext context);

    interface RuleBuilder<T extends Rule> extends ModelBuilder<T>{
        void setPredicate(Predicate predicate);
        void addUnifyValue(Value val);
        List<Value> getUnifyArgs();
        T close();
        boolean isClosed();
    }
}
