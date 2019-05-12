package ru.prolog.syntaxmodel;

/**
 * Катагории токенов.
 */
public enum TokenKind {
    /**
     * Токены, которые не имеют значения для программы и могут быть пропущены
     * (комментарии, пробелы, переносы строк)
     */
    IGNORED(false),
    /**
     * Токены, имеющие смысл при синтаксическом разборе программы, для разделения конструкций языка
     * (запятые, точки с запятой, круглые и квадратные скобки)
     */
    SYNTAX(true),
    /**
     * Токены, имеющие смысловоке содержание.
     * (имена, константы, математические операторы)
     */
    SEMANTIC(true),
    /**
     * Цепочка символов, которую не удалось распознать как токен
     */
    UNKNOWN(false);

    private final boolean meaning;

    TokenKind(boolean meaning) {
        this.meaning = meaning;
    }

    public boolean isMeaning() {
        return meaning;
    }
}
