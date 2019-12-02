package ru.prolog.syntaxmodel.source;

/**
 * Упрощённый источник кода для тестов, код неизменяемый.
 */
public class UnmodifiableStringSourceCode extends CodeSource {
    private final String text;

    public UnmodifiableStringSourceCode(String text) {
        this.text = text;
    }

    @Override
    public boolean actual() {
        return true;
    }

    @Override
    public String getTreeSource() {
        return text;
    }

    @Override
    public String getUpdatedSource() {
        return text;
    }
}
