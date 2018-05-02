package ru.prolog.std.db;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.exceprions.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.rule.FactRule;
import ru.prolog.storage.database.Database;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;
import ru.prolog.values.functor.FunctorValueImpl;

import java.util.Collections;
import java.util.List;

class AbstractAssertPredicate extends AbstractPredicate {
    private boolean a;
    public AbstractAssertPredicate(String name, TypeStorage typeStorage, boolean a) {
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
