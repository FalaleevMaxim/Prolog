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

/**
 * Узел, состоящий из имени и списка аргументов в скобках через запятую (функтор, вызов предиката или левая часть правила).
 */
public class FunctorNode extends AbstractNode implements Bracketed, Separated {
    public static final Set<TokenType> FOLLOW_SET = Collections.unmodifiableSet(EnumSet.of(RB, COMMA));

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
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (!ofType(token, SYMBOL)) return FAIL;
        name = token;
        addChild(token);

        parseOptional(lexer, this::parseBrackets);
        return OK;
    }

    private ParsingResult parseBrackets(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, LB)) {
            lb = token;
            addChild(lb);
        } else {
            return FAIL;
        }

        while (true) {
            ParsingResult result = parseOptional(lexer, this::parseClosing);
            if (result.isOk()) return result;
            if (args.isEmpty()) {
                result = parseOptional(lexer, this::parseArg);
                if (!result.isOk()) {
                    addError(lb, true, "Expected closing ')' or value");
                    return OK; //ToDo использовать follow-set
                }
            } else {
                result = parseOptional(lexer, this::parseCommaAndArg);
                if (!result.isOk()) {
                    addError(children().get(children().size()-1), true, "Expected closing ')' or more args");
                    return OK; //ToDo использовать follow-set
                }
            }
        }
    }

    private ParsingResult parseClosing(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, RB)) {
            rb = token;
            addChild(token);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseArg(Lexer lexer) {
        ValueNode value = new ValueNode(this);
        if (value.parse(lexer).isOk()) {
            args.add(value);
            addChild(value);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseCommaAndArg(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, COMMA)) {
            commas.add(token);
            addChild(token);
        } else {
            return FAIL;
        }

        if(!parseOptional(lexer, this::parseArg).isOk()) {
            addError(token, true, "Expected value");
        }
        return OK;
    }

    @Override
    protected Set<TokenType> initialFollowSet() {
        return FOLLOW_SET;
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
