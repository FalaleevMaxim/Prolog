package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import static ru.prolog.syntaxmodel.TokenType.*;

public class BinaryMathExprNode extends AbstractNode { //ToDo Поменять способ парсинга математических выражений
    /**
     * Число слева
     */
    Token left;

    /**
     * Оператор '+', '-', '*', '/', 'div', 'mod'
     */
    Token operator;

    /**
     * Число справа
     */
    Token right;

    public BinaryMathExprNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        left = null;
        operator = null;
        right = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, INTEGER, REAL, VARIABLE)) return ParsingResult.fail();
        left = token;
        addChild(left);

        token = lexer.nextNonIgnored();
        if(!ofType(token, PLUS, MINUS, DIVIDE, STAR_MULTIPLY, DIV, MOD)) return ParsingResult.fail();
        operator = token;
        addChild(operator);

        if(!parseOptional(lexer, this::parseRight).isOk()) {
            addError(operator, true, "Expected number");
        }
        return ParsingResult.ok();
    }

    private ParsingResult parseRight(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, INTEGER, REAL, VARIABLE)) {
            right = token;
            addChild(right);
            return ParsingResult.ok();
        }
        return ParsingResult.fail();
    }
}
