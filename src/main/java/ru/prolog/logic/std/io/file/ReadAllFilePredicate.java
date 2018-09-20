package ru.prolog.logic.std.io.file;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.exceptions.FreeVariableException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.values.simple.SimpleValue;
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
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if (startWith > 0) return -1;
        Value arg = args.get(0);
        if (isFreeVariable(arg)) throw new FreeVariableException("File name is free variable", (Variable) arg);
        ErrorListener err = context.programContext().getErrorListeners();
        String fileName = (String) arg.getValue();
        File file = new File(fileName);
        if (!file.exists()) {
            err.println("File does not exist: " + file);
            return -1;
        }
        Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
        try {
            List<String> lines = Files.readAllLines(path);
            String ret = joinLines(lines);
            return args.get(1).unify(new SimpleValue(typeStorage.get("string"), ret)) ? 0 : -1;
        } catch (IOException e) {
            err.println("Error reading file:");
            err.println(e.toString());
            return -1;
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
