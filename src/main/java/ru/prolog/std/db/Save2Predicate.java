package ru.prolog.std.db;

import ru.prolog.logic.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.logic.etc.exceptions.runtime.PrologRuntimeException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;
import ru.prolog.logic.storage.type.TypeStorage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class Save2Predicate extends AbstractPredicate {

    public Save2Predicate(TypeStorage typeStorage) {
        super("save", Arrays.asList("string", "string"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        Value fileNameArg = args.get(0);
        if(isFreeVariable(fileNameArg))
            throw new FreeVariableException("File name for save is free variable", (Variable) fileNameArg);
        String fileName = (String) fileNameArg.getValue();
        Value dbNameArg = args.get(1);
        if(isFreeVariable(dbNameArg))
            throw new FreeVariableException("Database name for save is free variable", (Variable) dbNameArg);
        String dbName = (String) dbNameArg.getValue();
        if(fileName==null) throw new FreeVariableException("File name is free variable", (Variable) fileNameArg);
        try {
            PrintWriter pw = new PrintWriter(fileName);
            pw.print(context.programContext().database().save(dbName));
            pw.close();
            return 0;
        } catch (FileNotFoundException e) {
            throw new PrologRuntimeException("Can not open file "+fileName, e);
        }
    }
}