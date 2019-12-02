package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.recognizers.Hint;

import java.util.function.Predicate;

/**
 * Распознаёт токен-вещественное число.
 * Не распознаёт токен, если в нём нет точки. Число без точки целое, и распознавать его нужно с помощью {@link IntegerRecognizer}
 * Знаки + или - не входят в состав числа, т.к. могут быть отдельными токенами даже находясь вплотную к цифрам.
 * Например, знаки в выражениях p(-1.1) и X=Y-1.1 имеют разный смысл. Во втором случае знак входит в состав грамматической констркции (математического выражения), а не числа.
 * Поэтому лучше сделать знак отдельным токеном, а число со знаком сделть синтаксической конструкцией.
 */
public class RealRecognizer extends TokenRecognizer {

    @Override
    public Token recognize(CharSequence code) {
        if (code.length() == 0) return null;
        int i = 0;
        Predicate<Character> digitCondition = c -> c >= '0' && c <= '9';

        int beforePoint = matchCharacters(code.subSequence(i, code.length() - 1), digitCondition);
        i += beforePoint;
        //Если нет точки, то тип токена не real, а integer, поэтому токен не распознан как real.
        if (code.charAt(i) != '.') return null;
        i++;
        int afterPoint = (i == code.length()) ? 0 : matchCharacters(code.subSequence(i, code.length()), digitCondition);
        if (afterPoint == 0 && beforePoint == 0) {
            return partialTokenOf(tokenText(code, i), new Hint("No digits before and after point", null));
        }
        return tokenOf(tokenText(code, i + afterPoint));
    }
}
