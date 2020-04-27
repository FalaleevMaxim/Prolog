package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Bracketed;
import ru.prolog.syntaxmodel.tree.interfaces.Named;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Узел описания функтора или предиката
 */
public class FunctorDefNode extends AbstractNode implements Named, Bracketed, Separated {
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
    private List<Token> commas = new ArrayList<>();

    /**
     * Типы аргументов
     */
    private List<TypeNameNode> argTypes = new ArrayList<>();

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
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.SYMBOL)) {
            name = token;
            addChild(name);
        } else {
            return false;
        }

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
            if (argTypes.isEmpty()) {
                if (!parseOptional(lexer, this::parseType)) {
                    valid = false;
                    return true; //ToDo вместо return пропускать токены пока не найдётся закрывающая скобка
                }
            } else {
                if (!parseOptional(lexer, this::parseCommaAndType)) {
                    valid = false;
                    return true; //ToDo вместо return пропускать токены пока не найдётся закрывающая скобка
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

    private boolean parseType(Lexer lexer) {
        TypeNameNode typeName = new TypeNameNode(this);
        if (typeName.parse(lexer)) {
            argTypes.add(typeName);
            addChild(typeName);
            return true;
        }
        return false;
    }

    private boolean parseCommaAndType(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.COMMA)) {
            commas.add(token);
            addChild(token);
        } else {
            return false;
        }

        TypeNameNode typeName = new TypeNameNode(this);
        if (typeName.parse(lexer)) {
            argTypes.add(typeName);
            addChild(typeName);
        } else {
            valid = false;
        }
        return true;
    }

    @Override
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
