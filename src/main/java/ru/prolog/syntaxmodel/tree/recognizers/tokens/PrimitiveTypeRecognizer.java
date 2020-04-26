package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.Token;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Распознаватель имён примитивных типов.
 */
public class PrimitiveTypeRecognizer extends TokenRecognizer {
    public static final List<String> PRIMITIVES = Collections.unmodifiableList(
            Arrays.asList("integer", "real", "char", "string", "symbol"));

    @Override
    public Token recognize(CharSequence code) {
        return PRIMITIVES.stream()
                .filter(s -> matchText(code, s))
                .filter(s -> !(code.length() > s.length() && SymbolRecognizer.OTHER_CHARS.test(code.charAt(s.length()))))
                .map(this::tokenOf)
                .findFirst()
                .orElse(null);
    }
}
