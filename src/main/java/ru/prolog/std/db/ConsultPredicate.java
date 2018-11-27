package ru.prolog.std.db;

import ru.prolog.compiler.CompileException;
import ru.prolog.compiler.PrologCompiler;
import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.etc.exceptions.runtime.PrologRuntimeException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.rule.FactRule;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.database.Database;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsultPredicate extends AbstractPredicate {
    public ConsultPredicate(TypeStorage typeStorage) {
        super("consult", Collections.singletonList("string"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        Value arg = args.get(0);
        if(isFreeVariable(arg))
            throw new FreeVariableException("File name for consult is free variable", (Variable) arg);
        String fileName = (String) arg.getContent();

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
            return PredicateResult.LAST_RESULT;
        } catch (IOException e) {
            throw new PrologRuntimeException("Error reading database file", e);
        }
    }
}
