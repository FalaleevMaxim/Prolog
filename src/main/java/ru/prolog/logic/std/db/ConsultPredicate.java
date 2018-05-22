package ru.prolog.logic.std.db;

import ru.prolog.compiler.CompileException;
import ru.prolog.compiler.PrologCompiler;
import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.exceptions.FreeVariableException;
import ru.prolog.logic.exceptions.PrologRuntimeException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.model.rule.FactRule;
import ru.prolog.logic.storage.database.Database;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsultPredicate extends AbstractPredicate {
    public ConsultPredicate(TypeStorage typeStorage) {
        super("consult", Collections.singletonList("string"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;

        Value arg = args.get(0);
        if(isFreeVariable(arg))
            throw new FreeVariableException("File name for consult is free variable", (Variable) arg);
        String fileName = (String) arg.getValue();

        try {
            List<CompileException> exceptions = new ArrayList<>();
            List<FactRule> facts = PrologCompiler.consult(context.programContext().program(), fileName, exceptions);
            if(!exceptions.isEmpty()){
                for (CompileException e : exceptions) {
                    context.programContext().getErrorListeners().println(e.toString());
                }
                throw new PrologRuntimeException("Error in database file");
            }

            Database database = context.programContext().database();
            for (FactRule fact : facts) {
                database.assertz(fact);
            }
            return 0;
        } catch (IOException e) {
            throw new PrologRuntimeException("Error reading database file", e);
        }
    }
}
