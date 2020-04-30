package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Bracketed;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import java.util.*;

import static ru.prolog.syntaxmodel.TokenType.*;
import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

public class ListValueNode extends AbstractNode implements Bracketed, Separated {
    public static final Set<TokenType> FOLLOW_SET = Collections.unmodifiableSet(
            EnumSet.of(RSQB, COMMA, TAILSEP));

    /**
     * Открывающая квадратная скобка
     */
    private Token lb;
    /**
     * Закрывающая квадратная скобка
     */
    private Token rb;
    /**
     * Запятые между элементами (головами) списка
     */
    private final List<Token> commas = new ArrayList<>();
    /**
     * Элементы (головы) списка
     */
    private final List<ValueNode> heads = new ArrayList<>();
    /**
     * Символ '|', отделяющий хвост
     */
    private Token tailSep;
    /**
     * Переменная-хвост
     */
    private Token tailVar;

    public ListValueNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        lb = null;
        rb = null;
        commas.clear();
        heads.clear();
        tailSep = null;
        tailVar = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) { //ToDo использовать follow-set
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, TokenType.LSQB)) return FAIL;
        lb = token;
        addChild(token);

        while (true) {
            if(parseOptional(lexer, this::parseClosing).isOk()) return OK;
            if(heads.isEmpty()) {
                if(!parseOptional(lexer, this::parseValue).isOk()) {
                    addError(lb, true, "Expected '|', ']' or list element");
                    return OK;
                }
            } else {
                if(parseOptional(lexer, this::parseTail).isOk()) return OK;
                if(!parseOptional(lexer, this::parseCommaAndValue).isOk()) {
                    addError(heads.get(heads.size()-1), true, "Expected '|', ']' or another element of list");
                    return OK;
                }
            }
        }
    }

    private ParsingResult parseClosing(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.RSQB)) {
            rb = token;
            addChild(token);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseValue(Lexer lexer) {
        ValueNode value = new ValueNode(this);
        if(value.parse(lexer).isOk()) {
            heads.add(value);
            addChild(value);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseCommaAndValue(Lexer lexer) {
        ParsingResult parseComma = parseOptional(lexer, this::parseComma);
        ParsingResult parseValue = parseOptional(lexer, this::parseValue);
        if(parseComma.isOk() || parseValue.isOk()) {
            if (!parseComma.isOk()) {
                addError(heads.get(heads.size()-1), false, "Expected ',' before list element");
            }
            if (!parseValue.isOk()) {
                addError(commas.get(commas.size()-1), true, "Expected list element");
            }
        }
        if(parseValue.isOk()) return parseValue;
        return parseComma;
    }

    private ParsingResult parseComma(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.COMMA)) {
            commas.add(token);
            addChild(token);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseTail(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (!ofType(token, TokenType.TAILSEP)) return FAIL;
        tailSep = token;
        addChild(token);

        if(!parseOptional(lexer, this::parseVar).isOk()) {
            addError(tailSep, true, "Expected variable");
            return OK;
        }

        if(!parseOptional(lexer, this::parseClosing).isOk()) {
            addError(tailVar, true, "Expected ']'");
        }
        return OK;
    }

    private ParsingResult parseVar(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.VARIABLE, TokenType.ANONYMOUS)) {
            tailVar = token;
            addChild(token);
            return OK;
        }
        return FAIL;
    }

    @Override
    public Token getLb() {
        return lb;
    }

    @Override
    public Token getRb() {
        return rb;
    }

    public List<Token> getCommas() {
        return commas;
    }

    public List<ValueNode> getHeads() {
        return heads;
    }

    public Token getTailSep() {
        return tailSep;
    }

    public Token getTailVar() {
        return tailVar;
    }

    @Override
    public List<Token> getSeparators() {
        if(tailSep == null) return getCommas();
        ArrayList<Token> separators = new ArrayList<>(getCommas());
        separators.add(tailSep);
        return separators;
    }
}
