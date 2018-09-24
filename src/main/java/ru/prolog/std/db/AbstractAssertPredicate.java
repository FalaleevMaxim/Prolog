package ru.prolog.std.db;

import ru.prolog.logic.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.model.predicate.DatabasePredicate;
import ru.prolog.logic.model.rule.FactRule;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;
import ru.prolog.logic.runtime.values.functor.FunctorValueImpl;
import ru.prolog.logic.storage.database.Database;
import ru.prolog.logic.storage.type.TypeStorage;

import java.util.Collections;
import java.util.List;

public abstract class AbstractAssertPredicate extends AbstractPredicate {
    private boolean a;
    AbstractAssertPredicate(String name, TypeStorage typeStorage, boolean a) {
        super(name, Collections.singletonList("database"), typeStorage);
        this.a=a;
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
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
        return 0;
    }
}
