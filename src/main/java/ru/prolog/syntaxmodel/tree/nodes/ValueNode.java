package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import java.util.*;
import java.util.stream.Collectors;

import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

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
    protected ParsingResult parseInternal(Lexer lexer) {
        ParsingResult result = parseOptional(lexer, this::parseFunctor);
        if(result.isOk()) return result;
        result = parseOptional(lexer, this::parseList);
        if(result.isOk()) return result;
        return parseOptional(lexer, this::parseSimpleValue);
    }

    private ParsingResult parseFunctor(Lexer lexer) {
        FunctorNode functorNode = new FunctorNode(this);
        if(functorNode.parse(lexer).isOk()) {
            functor = functorNode;
            addChild(functor);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseList(Lexer lexer) {
        ListValueNode listValueNode = new ListValueNode(this);
        if(listValueNode.parse(lexer).isOk()) {
            list = listValueNode;
            addChild(list);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseSimpleValue(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        //  списке нет SYMBOL, потому что он распознаётся как функтор без скобок
        if(ofType(token, TokenType.VARIABLE, TokenType.ANONYMOUS, TokenType.INTEGER, TokenType.REAL, TokenType.STRING, TokenType.CHAR)) {
            simpleValue = token;
            addChild(token);
            return OK;
        }
        return FAIL;
    }

    /**
     * Возвращает этот узел и все вложенные в этот него узлы {@link ValueNode}.
     * Результат не включает хвосты списков, поскольку они являются токенами, а не {@link ValueNode}
     */
    public Collection<ValueNode> getInnerValues() {
        if(isSimpleValue()) return Collections.singletonList(this);
        List<ValueNode> valueNodes = new ArrayList<>();
        valueNodes.add(this);
        if(isList()) valueNodes.addAll(list.getHeads());
        if(isFunctor()) functor.getArgs().stream()
                .map(ValueNode::getInnerValues)
                .forEach(valueNodes::addAll);
        return valueNodes;
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
