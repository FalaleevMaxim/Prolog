package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.exceprions.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.rule.FactRule;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;
import ru.prolog.values.functor.FunctorValue;

import java.util.Collections;
import java.util.List;

public class AssertPredicate extends AbstractPredicate {
    public AssertPredicate(TypeStorage typeStorage) {
        super("assert", Collections.singletonList("database"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        FunctorValue fact = (FunctorValue) args.get(0);
        List<Variable> variables = fact.innerFreeVariables();
        if(!variables.isEmpty())
            throw new FreeVariableException("Free variables not allowed in assert", variables.get(0));
        FactRule rule = new FactRule();
        DatabasePredicate predicate = context.programContext().database().get(fact.getFunctorName());
        rule.setPredicate(predicate);
        for(Value arg : fact.getSubObjects()){
            rule.addUnifyArg(arg.toModel());
        }
        rule.fix();
        context.programContext().database().assertz(rule);
        return 0;
    }
}
