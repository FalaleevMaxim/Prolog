package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.prolog.syntaxmodel.TokenType.*;

/**
 * Описание типа данных из domains
 */
public class TypeDefNode extends AbstractNode implements Separated {
    /**
     * Имена типа слева
     */
    private List<Token> typeNames = new ArrayList<>();

    /**
     * Запятые, разделяющие имена типа
     */
    private List<Token> commas = new ArrayList<>();

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
    public boolean isPrimitive() {
        return primitive != null;
    }

    /**
     * @return Является ли тип списковым
     */
    public boolean isList() {
        return listType != null;
    }

    /**
     * @return Является ли тип составным
     */
    public boolean isCompound() {
        return compoundType != null;
    }

    public TypeDefNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        typeNames.clear();
        commas.clear();
        equalsToken = null;
        primitive = null;
        listType = null;
        compoundType = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        if(!parseName(lexer)) return false;

        while (parseOptional(lexer, this::parseCommaAndName));

        Token token = lexer.nextNonIgnored();
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

    private boolean parseName(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, SYMBOL)) {
            typeNames.add(token);
            addChild(token);
            return true;
        } return false;
    }

    private boolean parseComma(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, COMMA)) {
            commas.add(token);
            addChild(token);
            return true;
        } return false;
    }

    private boolean parseCommaAndName(Lexer lexer) {
        boolean parseComma = parseOptional(lexer, this::parseComma);
        boolean parseName = parseOptional(lexer, this::parseName);
        if(!parseComma && !parseName) {
            return false;
        }

        if(!parseComma || !parseName) {
            valid = false;
        }
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

    public List<Token> getTypeNames() {
        return Collections.unmodifiableList(typeNames);
    }

    public List<Token> getCommas() {
        return Collections.unmodifiableList(commas);
    }

    @Override
    public List<Token> getSeparators() {
        return getCommas();
    }
}
