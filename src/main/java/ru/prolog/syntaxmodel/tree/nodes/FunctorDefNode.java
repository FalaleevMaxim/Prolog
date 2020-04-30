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
 * Узел описания функтора или предиката
 */
public class FunctorDefNode extends AbstractNode implements Bracketed, Separated {
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
     * Запятые, разделяющие типы аргументов
     */
    private final List<Token> commas = new ArrayList<>();

    /**
     * Типы аргументов
     */
    private final List<TypeNameNode> argTypes = new ArrayList<>();

    public FunctorDefNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        name = null;
        lb = null;
        rb = null;
        commas.clear();
        argTypes.clear();
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, SYMBOL)) {
            name = token;
            addChild(name);
        } else {
            return FAIL;
        }

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
            if (parseOptional(lexer, this::parseClosing).isOk()) return OK;
            if (argTypes.isEmpty()) {
                if (!parseOptional(lexer, this::parseType).isOk()) {
                    addError(lb, true, "Expected closing ')' or args");
                    return OK; //ToDo вместо return использовать follow-set
                }
            } else {
                if (!parseOptional(lexer, this::parseCommaAndType).isOk()) {
                    addError(children().get(children().size()-1), true, "Expected closing ')' or more args");
                    return OK; //ToDo вместо return использовать follow-set
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

    private ParsingResult parseType(Lexer lexer) {
        TypeNameNode typeName = new TypeNameNode(this);
        if (typeName.parse(lexer).isOk()) {
            argTypes.add(typeName);
            addChild(typeName);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseCommaAndType(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, COMMA)) {
            commas.add(token);
            addChild(token);
        } else {
            return FAIL;
        }

        if(!parseOptional(lexer, this::parseType).isOk()) {
            addError(token, true, "Expected type name");
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

    @Override
    public List<Token> getSeparators() {
        return getCommas();
    }

    public List<Token> getCommas() {
        return Collections.unmodifiableList(commas);
    }

    public List<TypeNameNode> getArgTypes() {
        return Collections.unmodifiableList(argTypes);
    }
}
