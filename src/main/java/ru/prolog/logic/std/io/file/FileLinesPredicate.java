package ru.prolog.logic.std.io.file;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.exceptions.FreeVariableException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.values.simple.SimpleValue;
import ru.prolog.util.io.ErrorListener;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FileLinesPredicate extends AbstractPredicate {
    public FileLinesPredicate(TypeStorage typeStorage) {
        super("fileLines", Arrays.asList("string", "string"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        final String key = "fin";
        BufferedReader in = (BufferedReader) context.getContextData(key);

        ErrorListener err = context.programContext().getErrorListeners();
        if (in == null) {
            Value arg = args.get(0);
            if (isFreeVariable(arg)) throw new FreeVariableException("File name is free variable", (Variable) arg);

            String fileName = (String) arg.getValue();
            File file = new File(fileName);
            if (!file.exists()) {
                err.println("File does not exist: " + file);
                return -1;
            }

            try {
                in = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                err.println("Error opening file " + file);
                err.println(e.toString());
                return -1;
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
            if (ret == null) return -1;
            if (args.get(1).unify(new SimpleValue(typeStorage.get("string"), ret))) {
                return startWith;
            }
            ++startWith;
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
