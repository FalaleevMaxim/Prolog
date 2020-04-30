package ru.prolog.syntaxmodel.tree.misc;

import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;

public class ParsingResult {
    public static final ParsingResult OK = new ParsingResult(true);
    public static final ParsingResult FAIL = new ParsingResult(false);

    public static ParsingResult ok() {
        return OK;
    }

    public static ParsingResult fail() {
        return FAIL;
    }

    /**
     * Результат парсинга, удачно или нет.
     *
     * Даже если ок, следует проверить {@link #resume}.
     * Возможно, что узел смог распознаться частично, но дальше достиг символа из follow-set уровнями ниже
     */
    private final boolean ok;

    /**
     * Для какого узла нашёлся токен по follow-set
     */
    private final AbstractNode resume;

    /**
     * Токен, найденный по follow-set
     */
    private final Token following;

    public ParsingResult(boolean ok) {
        this.ok = ok;
        this.resume = null;
        this.following = null;
    }

    public ParsingResult(boolean ok, AbstractNode resume, Token following) {
        this.ok = ok;
        this.resume = resume;
        this.following = following;
    }

    public boolean isOk() {
        return ok;
    }

    public AbstractNode getResume() {
        return resume;
    }

    public Token getFollowing() {
        return following;
    }
}
