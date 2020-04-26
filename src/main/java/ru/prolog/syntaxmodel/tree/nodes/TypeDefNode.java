package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Named;

import static ru.prolog.syntaxmodel.TokenType.*;

/**
 * Описание типа данных из domains
 */
public class TypeDefNode extends AbstractNode implements Named {
    /**
     * Имя типа слева
     */
    private Token typeName;
    /**
     * Символ =
     */
    private Token equalsToken;
    /**
     * Имя примитивного типа справа от =
     */
    private Token primitive;
    /**
     * Описание спискового типа справа от =
     */
    private ListTypeNode listType;
    /**
     * Описание составного типа справа от =
     */
    private CompoundTypeNode compoundType;

    /**
     * @return Является ли тип примитивным
     */
    private boolean isPrimitive() {
        return primitive != null;
    }

    /**
     * @return Является ли тип списковым
     */
    private boolean isList() {
        return listType != null;
    }

    /**
     * @return Является ли тип составным
     */
    private boolean isCompound() {
        return compoundType != null;
    }

    public TypeDefNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        typeName = null;
        equalsToken = null;
        primitive = null;
        listType = null;
        compoundType = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, SYMBOL)) {
            typeName = token;
            addChild(typeName);
        } else return false;

        token = lexer.nextNonIgnored();
        if (ofType(token, EQUALS)) {
            equalsToken = token;
            addChild(equalsToken);
        } else return false;

        if (parseOptional(lexer, this::parseList)) return true;
        if (parseOptional(lexer, this::parsePrimitive)) return true;
        if (parseOptional(lexer, this::parseCompound)) return true;
        valid = false;
        return true;
    }

    private boolean parseList(Lexer lexer) {
        ListTypeNode listTypeNode = new ListTypeNode(this);
        if (listTypeNode.parse(lexer)) {
            listType = listTypeNode;
            addChild(listTypeNode);
            return true;
        }
        return false;
    }

    private boolean parsePrimitive(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (token == null) return false;
        if (token.getTokenType() == PRIMITIVE) {
            primitive = token;
            addChild(token);
            return true;
        }
        return false;
    }

    private boolean parseCompound(Lexer lexer) {
        CompoundTypeNode compoundTypeNode = new CompoundTypeNode(this);
        if (compoundTypeNode.parse(lexer)) {
            compoundType = compoundTypeNode;
            addChild(compoundTypeNode);
            return true;
        }
        return false;
    }

    @Override
    public Token getName() {
        return typeName;
    }

    public Token getEqualsToken() {
        return equalsToken;
    }

    public Token getPrimitive() {
        return primitive;
    }

    public ListTypeNode getListType() {
        return listType;
    }

    public CompoundTypeNode getCompoundType() {
        return compoundType;
    }
}
