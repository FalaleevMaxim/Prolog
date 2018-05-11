package ru.prolog.logic.std;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.simple.SimpleValue;

import java.util.Arrays;
import java.util.List;

public class FormatPredicate extends AbstractPredicate {
    public FormatPredicate(TypeStorage typeStorage) {
        super("format", Arrays.asList("string", "string", "..."), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith==1) return -1;
        String format = (String)args.get(1).getValue();
        StringBuilder out = new StringBuilder();
        int i = 2;
        for(char c : format.toCharArray()){
            if(c=='%'){
                if(i<args.size())
                    out.append(args.get(i++));
            }
            else out.append(c);
        }
        new SimpleValue(typeStorage.get("string"), out.toString()).unify(args.get(0));
        return 0;
    }
}
