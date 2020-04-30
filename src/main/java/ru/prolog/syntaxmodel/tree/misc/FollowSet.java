package ru.prolog.syntaxmodel.tree.misc;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;

import java.util.Set;

/**
 * Follow-set узла
 */
public class FollowSet {
    /**
     * Узел (уровень парсера), которому принадлежит follow-set
     */
    private final AbstractNode node;
    /**
     * Ожидаемые узлом типы
     */
    private final Set<TokenType> followSet;

    /**
     * Токен, найденный по follow-set
     */
    private Token found;

    public FollowSet(AbstractNode node, Set<TokenType> followSet) {
        this.node = node;
        this.followSet = followSet;
    }

    public Set<TokenType> getFollowSet() {
        return followSet;
    }

    public Token getFound() {
        return found;
    }

    public void setFound(Token found) {
        this.found = found;
    }

    public AbstractNode getNode() {
        return node;
    }
}
