package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Bracketed;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;

import java.util.ArrayList;
import java.util.List;

public class ListValueNode extends AbstractNode implements Bracketed, Separated {
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
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, TokenType.LSQB)) return false;
        lb = token;
        addChild(token);

        while (true) {
            if(parseOptional(lexer, this::parseClosing)) return true;
            if(heads.isEmpty()) {
                if(!parseOptional(lexer, this::parseValue)) {
                    valid = false;
                    return true; //ToDo вместо этого пропускать токены пока не найдётся | или ]
                }
            } else {
                if(parseOptional(lexer, this::parseTail)) return true;
                if(!parseOptional(lexer, this::parseCommaAndValue)) {
                    valid = false;
                    return true;
                }
            }
        }
    }

    private boolean parseClosing(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.RSQB)) {
            rb = token;
            addChild(token);
            return true;
        }
        return false;
    }

    private boolean parseValue(Lexer lexer) {
        ValueNode value = new ValueNode(this);
        if(value.parse(lexer)) {
            heads.add(value);
            addChild(value);
            return true;
        }
        return false;
    }

    private boolean parseCommaAndValue(Lexer lexer) {
        boolean parseComma = parseOptional(lexer, this::parseComma);
        boolean parseValue = parseOptional(lexer, this::parseValue);
        if(!parseComma || !parseValue) valid = false;
        return parseComma || parseValue;
    }

    private boolean parseComma(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.COMMA)) {
            commas.add(token);
            addChild(token);
            return true;
        }
        return false;
    }

    private boolean parseTail(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (!ofType(token, TokenType.TAILSEP)) return false;
        tailSep = token;
        addChild(token);

        if(!parseOptional(lexer, this::parseVar)) {
            valid = false;
            return true;
        }

        if(!parseOptional(lexer, this::parseClosing)) {
            valid = false;
        }
        return true;
    }

    private boolean parseVar(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.VARIABLE, TokenType.ANONYMOUS)) {
            tailVar = token;
            addChild(token);
            return true;
        }
        return false;
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
