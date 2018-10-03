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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ReadAllFilePredicate extends AbstractPredicate {
    public ReadAllFilePredicate(TypeStorage typeStorage) {
        super("readAllFile", Arrays.asList("string", "string"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        Value arg = args.get(0);
        if (isFreeVariable(arg)) throw new FreeVariableException("File name is free variable", (Variable) arg);
        ErrorListener err = context.programContext().getErrorListeners();
        String fileName = (String) arg.getValue();
        File file = new File(fileName);
        if (!file.exists()) {
            err.println("File does not exist: " + file);
            return PredicateResult.FAIL;
        }
        Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
        try {
            List<String> lines = Files.readAllLines(path);
            String ret = joinLines(lines);
            return args.get(1).unify(new SimpleValue(typeStorage.get("string"), ret))
                    ? PredicateResult.LAST_RESULT
                    : PredicateResult.FAIL;
        } catch (IOException e) {
            err.println("Error reading file:");
            err.println(e.toString());
            return PredicateResult.FAIL;
        }
    }

    private String joinLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) return "";
        StringBuilder sb = new StringBuilder(lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            sb.append('\n').append(lines.get(i));
        }
        return sb.toString();
    }
}
