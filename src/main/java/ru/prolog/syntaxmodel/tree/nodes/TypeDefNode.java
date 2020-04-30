package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import java.util.*;

import static ru.prolog.syntaxmodel.TokenType.*;
import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

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
    protected ParsingResult parseInternal(Lexer lexer) { //ToDo использовать follow-set
        if(!parseName(lexer).isOk()) return FAIL;

        while (parseOptional(lexer, this::parseCommaAndName).isOk());

        Token token = lexer.nextNonIgnored();
        if (ofType(token, EQUALS)) {
            equalsToken = token;
            addChild(equalsToken);
        } else return FAIL;

        ParsingResult result = parseOptional(lexer, this::parseList);
        if (result.isOk()) return result;
        result = parseOptional(lexer, this::parsePrimitive);
        if (result.isOk()) return result;
        result = parseOptional(lexer, this::parseCompound);
        if (result.isOk()) return result;
        addError(equalsToken, true, "Expected type definition");
        return OK;
    }

    private ParsingResult parseName(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, SYMBOL)) {
            typeNames.add(token);
            addChild(token);
            return OK;
        } return FAIL;
    }

    private ParsingResult parseComma(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, COMMA)) {
            commas.add(token);
            addChild(token);
            return OK;
        } return FAIL;
    }

    private ParsingResult parseCommaAndName(Lexer lexer) {
        ParsingResult parseComma = parseOptional(lexer, this::parseComma);
        ParsingResult parseName = parseOptional(lexer, this::parseName);
        if(!parseComma.isOk() && !parseName.isOk()) {
            return FAIL;
        }

        if(!parseComma.isOk()) {
            addError(typeNames.get(typeNames.size()-1), false, "Expected ',' before type name");
        }

        if(!parseName.isOk()) {
            addError(commas.get(commas.size() - 1), true, "Expected type name");
        }
        return OK;
    }

    private ParsingResult parseList(Lexer lexer) {
        ListTypeNode listTypeNode = new ListTypeNode(this);
        if (listTypeNode.parse(lexer).isOk()) {
            listType = listTypeNode;
            addChild(listTypeNode);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parsePrimitive(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (token == null) return FAIL;
        if (token.getTokenType() == PRIMITIVE) {
            primitive = token;
            addChild(token);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseCompound(Lexer lexer) {
        return parseChildNode(new CompoundTypeNode(this), lexer, n->{compoundType = n;});
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
