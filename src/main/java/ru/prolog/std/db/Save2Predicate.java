package ru.prolog.std.db;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.etc.exceptions.runtime.PrologRuntimeException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class Save2Predicate extends AbstractPredicate {

    public Save2Predicate(TypeStorage typeStorage) {
        super("save", Arrays.asList("string", "string"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
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
            return PredicateResult.LAST_RESULT;
        } catch (FileNotFoundException e) {
            throw new PrologRuntimeException("Can not open file "+fileName, e);
        }
    }
}