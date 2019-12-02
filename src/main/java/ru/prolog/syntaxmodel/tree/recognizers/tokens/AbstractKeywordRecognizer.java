package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.Token;

/**
 * Распознаёт конкретный текст в коде.
 */
public abstract class AbstractKeywordRecognizer extends TokenRecognizer {
    /**
     * Текст ключевого слова.
     */
    private final String keyword;
    /**
     * Является ли {@link #keyword} словом (состоит только из букв)
     */
    private final boolean isWord;

    public String getKeyword() {
        return keyword;
    }

    public boolean isWord() {
        return isWord;
    }

    public AbstractKeywordRecognizer(String keyword) {
        this.keyword = keyword;
        isWord = keyword.chars().allMatch(Character::isLetter);
    }

    @Override
    public Token recognize(CharSequence code) {
        //Начало текста совпадает с ключевым словом, а следующий за ключевым словом символ не является текстовым
        //Если за ключевым словом идут символы, допустимые в SymbolRecognizer, то этот токен будет символьным, а не ключевое слово.
        //Например, слово goals должно быть распознано как 1 токен символьного типа, а не как ключевое слово goal и символьный токен s.
        //Однако если текст не является словом (например, распознаются знаки +, -, :-, !), то он точно не может быть символьным токеном, и после него допустимы любые символы.
        return matchText(code, keyword) && !(code.length() > keyword.length() && isWord() && SymbolRecognizer.OTHER_CHARS.test(code.charAt(keyword.length()))) ?
                tokenOf(keyword) :
                null;
    }
}
