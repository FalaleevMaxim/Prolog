package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;

public class ValueNode extends AbstractNode {
    private Token simpleValue;
    private FunctorNode functor;
    private ListValueNode list;

    public ValueNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        simpleValue = null;
        functor = null;
        list = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        return parseOptional(lexer, this::parseFunctor) || parseOptional(lexer, this::parseList) || parseOptional(lexer, this::parseSimpleValue);
    }

    private boolean parseFunctor(Lexer lexer) {
        FunctorNode functorNode = new FunctorNode(this);
        if(functorNode.parse(lexer)) {
            functor = functorNode;
            addChild(functor);
            return true;
        }
        return false;
    }

    private boolean parseList(Lexer lexer) {
        ListValueNode listValueNode = new ListValueNode(this);
        if(listValueNode.parse(lexer)) {
            list = listValueNode;
            addChild(list);
            return true;
        }
        return false;
    }

    private boolean parseSimpleValue(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        //  списке нет SYMBOL, потому что он распознаётся как функтор без скобок
        if(ofType(token, TokenType.VARIABLE, TokenType.ANONYMOUS, TokenType.INTEGER, TokenType.REAL, TokenType.STRING, TokenType.CHAR)) {
            simpleValue = token;
            addChild(token);
            return true;
        }
        return false;
    }

    public boolean isVariable() {
        return ofType(simpleValue, TokenType.VARIABLE);
    }

    public boolean isAnonymousVariable() {
        return ofType(simpleValue, TokenType.ANONYMOUS);
    }

    public boolean isNumber() {
        return ofType(simpleValue, TokenType.INTEGER, TokenType.REAL);
    }

    public boolean isReal() {
        return ofType(simpleValue, TokenType.REAL);
    }

    public boolean isInteger() {
        return ofType(simpleValue, TokenType.INTEGER);
    }

    public boolean isSimpleValue() {
        return simpleValue != null;
    }

    public boolean isChar() {
        return ofType(simpleValue, TokenType.CHAR);
    }

    public boolean isString() {
        return ofType(simpleValue, TokenType.STRING);
    }

    public boolean isFunctor() {
        return functor != null;
    }

    public boolean isList() {
        return list != null;
    }

    public Token getSimpleValue() {
        return simpleValue;
    }

    public FunctorNode getFunctor() {
        return functor;
    }

    public ListValueNode getList() {
        return list;
    }
}
