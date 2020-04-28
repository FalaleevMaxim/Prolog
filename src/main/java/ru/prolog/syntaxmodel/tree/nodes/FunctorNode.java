package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Bracketed;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Узел, состоящий из имени и списка аргументов в скобках через запятую (функтор, вызов предиката или левая часть правила).
 */
public class FunctorNode extends AbstractNode implements Bracketed, Separated {
    /**
     * Имя функтора
     */
    private Token name;

    /**
     * Открывающая скобка
     */
    private Token lb;

    /**
     * Закрывающая скобка
     */
    private Token rb;

    /**
     * Аргументы функтора
     */
    private final List<ValueNode> args = new ArrayList<>();

    /**
     * Запятые, разделяющие аргументы.
     */
    private final List<Token> commas = new ArrayList<>();

    public FunctorNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        name = null;
        lb = null;
        rb = null;
        args.clear();
        commas.clear();
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (!ofType(token, TokenType.SYMBOL)) return false;
        name = token;
        addChild(token);

        parseOptional(lexer, this::parseBrackets);
        return true;
    }

    private boolean parseBrackets(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.LB)) {
            lb = token;
            addChild(lb);
        } else {
            return false;
        }

        while (true) {
            if (parseOptional(lexer, this::parseClosing)) return true;
            if (args.isEmpty()) {
                if (!parseOptional(lexer, this::parseArg)) {
                    valid = false;
                    return false; //ToDo вместо return пропускать токены пока не найдётся закрывающая скобка
                }
            } else {
                if (!parseOptional(lexer, this::parseCommaAndArg)) {
                    valid = false;
                    return false; //ToDo вместо return пропускать токены пока не найдётся закрывающая скобка
                }
            }
        }
    }

    private boolean parseClosing(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.RB)) {
            rb = token;
            addChild(token);
            return true;
        }
        return false;
    }

    private boolean parseArg(Lexer lexer) {
        ValueNode value = new ValueNode(this);
        if (value.parse(lexer)) {
            args.add(value);
            addChild(value);
            return true;
        }
        return false;
    }

    private boolean parseCommaAndArg(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.COMMA)) {
            commas.add(token);
            addChild(token);
        } else {
            return false;
        }

        if(!parseOptional(lexer, this::parseArg)) {
            valid = false;
        }
        return true;
    }

    public Token getName() {
        return name;
    }

    @Override
    public Token getLb() {
        return lb;
    }

    @Override
    public Token getRb() {
        return rb;
    }

    public List<ValueNode> getArgs() {
        return Collections.unmodifiableList(args);
    }

    public List<Token> getCommas() {
        return Collections.unmodifiableList(commas);
    }

    @Override
    public List<Token> getSeparators() {
        return getCommas();
    }
}
