package ru.prolog.model.rule;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.ModelBuilder;
import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.values.Value;
import ru.prolog.values.model.ValueModel;

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
