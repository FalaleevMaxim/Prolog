package ru.prolog.model.rule;

import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.values.ValueModel;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

public interface Rule extends ModelObject {
    Predicate getPredicate();
    List<ValueModel> getUnifyArgs();
    boolean run(List<Value> args, RuleContext context);
    boolean body(RuleContext context);
    boolean unifyArgs(List<Value> args, RuleContext context);

    //Setters (must disable after fix())
    void addUnifyArg(ValueModel arg);
    void setPredicate(Predicate predicate);
}
