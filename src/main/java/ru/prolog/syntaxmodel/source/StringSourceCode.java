package ru.prolog.syntaxmodel.source;

/**
 * Упрощённый источник кода для тестов, код неизменяемый.
 */
public class StringSourceCode extends CodeSource {
    private final String text;

    public StringSourceCode(String text) {
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
