package ru.prolog.logic.std.db;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.exceprions.FreeVariableException;
import ru.prolog.logic.exceprions.PrologRuntimeException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

public class SavePredicate extends AbstractPredicate {

    public SavePredicate(TypeStorage typeStorage) {
        super("save", Collections.singletonList("string"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        String fileName = (String) args.get(0).getValue();
        if(fileName==null) throw new FreeVariableException("File name is free variable", (Variable) args.get(0));
        try {
            PrintWriter pw = new PrintWriter(fileName);
            pw.print(context.programContext().database().save());
            pw.close();
            return 0;
        } catch (FileNotFoundException e) {
            throw new PrologRuntimeException("Can not open file "+fileName, e);
        }
    }
}
