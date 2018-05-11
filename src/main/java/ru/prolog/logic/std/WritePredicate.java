package ru.prolog.logic.std;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.exceprions.FreeVariableException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;

import java.util.Collections;
import java.util.List;

public class WritePredicate extends AbstractPredicate {
    public WritePredicate(TypeStorage typeStorage) {
        super("write", Collections.singletonList("..."), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        for(Value arg : args) {
            List<Variable> variables = arg.innerFreeVariables();
            if(!variables.isEmpty())
                throw new FreeVariableException("Free variable "+variables.get(0)+" in write predicate.", variables.get(0));
            System.out.print(arg);
        }
        return 0;
    }
}
