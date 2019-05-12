package ru.prolog.syntaxmodel.tree.recognizers;

public class Hint {
    /**
     * Текст ошибки
     */
    public String errorText;

    /**
     * Текст предложения по автодополнению
     */
    public String completion;

    public Hint(String errorText, String completion) {
        this.errorText = errorText;
        this.completion = completion;
    }
}
