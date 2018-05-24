package ru.prolog.logic.model.predicate;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.model.program.ProgramModule;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.storage.predicates.SinglePredicateStorage;
import ru.prolog.logic.storage.type.TypeStorageImpl;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.list.ListValue;
import ru.prolog.logic.values.list.PrologList;

import java.util.Arrays;
import java.util.List;

public class IntegersListPredicateModule extends ProgramModule {
    public IntegersListPredicateModule() {
        typeStorage = new TypeStorageImpl();
        typeStorage.addType("intList", new Type(typeStorage.get("integer"), "integer"));
        predicateStorage = new SinglePredicateStorage(new AbstractPredicate("intsAsList", Arrays.asList("intList","..."), typeStorage) {
            @Override
            public int run(PredicateContext context, List<Value> args, int startWith) {
                if(startWith>0) return -1;
                if(args.size()==1){
                    return new ListValue(typeStorage.get("intList")).unify(args.get(0))?0:1;
                }

                Value[] elements = new Value[args.size()-1];
                for (int i = 1; i < args.size(); i++) {
                    elements[i-1] = args.get(i);
                }
                return PrologList.asList(typeStorage.get("intList"), elements).unify(args.get(0))?0:-1;
            }
        });
    }
}
