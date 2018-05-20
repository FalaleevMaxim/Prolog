package ru.prolog.logic.model.rule;

import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.model.values.ValueModel;

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
