package ru.prolog.syntaxmodel.tree.recognizers.tokens.math;

import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.SymbolRecognizer;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.TokenRecognizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MathFunctionNameRecognizer extends TokenRecognizer {
    public static final List<String> FUNCTIONS = Collections.unmodifiableList(
            Arrays.asList("sin", "cos", "tan", "arctan", "ln", "log", "exp", "abs"));

    @Override
    public Token recognize(CharSequence code) {
        return FUNCTIONS.stream()
                .filter(s -> matchText(code, s))
                .filter(s -> !(code.length() > s.length() && SymbolRecognizer.OTHER_CHARS.test(code.charAt(s.length()))))
                .map(this::tokenOf)
                .findFirst()
                .orElse(null);
    }
}
