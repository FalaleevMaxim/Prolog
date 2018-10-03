package ru.prolog.std.db;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.rule.FactRule;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.database.Database;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.runtime.values.functor.FunctorValueImpl;

import java.util.Collections;
import java.util.List;

public abstract class AbstractAssertPredicate extends AbstractPredicate {
    private boolean a;
    AbstractAssertPredicate(String name, TypeStorage typeStorage, boolean a) {
        super(name, Collections.singletonList("database"), typeStorage);
        this.a=a;
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        FunctorValueImpl fact = (FunctorValueImpl) args.get(0);
        List<Variable> variables = fact.innerFreeVariables();
        if(!variables.isEmpty())
            throw new FreeVariableException("Free variables not allowed in assert", variables.get(0));
        FactRule rule = new FactRule();
        Database db = context.programContext().database();
        DatabasePredicate predicate = db.get(fact.getFunctorName());
        rule.setPredicate(predicate);
        for(Value arg : fact.getArgs()){
            rule.addUnifyArg(arg.toModel());
        }
        rule.fix();
        if(a) db.asserta(rule);
        else db.assertz(rule);
        return PredicateResult.LAST_RESULT;
    }
}
