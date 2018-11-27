package ru.prolog.std.io.file;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.runtime.values.simple.SimpleValue;
import ru.prolog.util.io.ErrorListener;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FileLinesPredicate extends AbstractPredicate {
    public FileLinesPredicate(TypeStorage typeStorage) {
        super("fileLines", Arrays.asList("string", "string"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        final String key = "fin";
        BufferedReader in = (BufferedReader) context.getContextData(key);

        ErrorListener err = context.programContext().getErrorListeners();
        if (in == null) {
            Value arg = args.get(0);
            if (isFreeVariable(arg)) throw new FreeVariableException("File name is free variable", (Variable) arg);

            String fileName = (String) arg.getContent();
            File file = new File(fileName);
            if (!file.exists()) {
                err.println("File does not exist: " + file);
                return PredicateResult.FAIL;
            }

            try {
                in = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                err.println("Error opening file " + file);
                err.println(e.toString());
                return PredicateResult.FAIL;
            }
            context.putContextData(key, in);
        }

        while (true) {
            String ret = null;
            try {
                ret = in.readLine();
            } catch (IOException e) {
                err.println("Error reading line");
                err.println(e.toString());
            }
            if (ret == null) return PredicateResult.FAIL;
            if (args.get(1).unify(new SimpleValue(typeStorage.get("string"), ret))) {
                return PredicateResult.NEXT_RESULT;
            }
        }
    }
}
