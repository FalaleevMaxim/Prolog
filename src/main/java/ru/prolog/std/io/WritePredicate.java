package ru.prolog.std.io;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.type.Type;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.util.io.OutputDevice;

import java.util.Collections;
import java.util.List;

public class WritePredicate extends AbstractPredicate {
    public WritePredicate(TypeStorage typeStorage) {
        super("write", Collections.singletonList("..."), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        for(Value arg : args) {
            List<Variable> variables = arg.innerFreeVariables();
            if(!variables.isEmpty())
                throw new FreeVariableException("Free variable "+variables.get(0)+" in write predicate.", variables.get(0));

            OutputDevice out = context.programContext().getOutputDevices();

            //toString() of SimpleValue for string or char type returns string with quotes and escape-characters.
            // That is good for printing in list or functor, but for just printing string or char you need to print value
            if(isStringOrChar(arg)) out.print(arg.getValue().toString());
            else out.print(arg.toString());
        }
        return PredicateResult.LAST_RESULT;
    }

    private boolean isStringOrChar(Value arg){
        if(!arg.getType().isPrimitive()) return false;
        Type.PrimitiveType primitive = arg.getType().getPrimitiveType();
        return primitive.isString() || primitive.isChar();
    }
}
