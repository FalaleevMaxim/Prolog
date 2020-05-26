package ru.prolog.syntaxmodel.tree.nodes.math;

import com.google.common.collect.ImmutableMap;
import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Bracketed;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;
import ru.prolog.syntaxmodel.tree.nodes.math.operations.*;

import java.util.*;

import static ru.prolog.syntaxmodel.TokenType.*;

/**
 * Главный узел математического выражения. Управляет парсингом всего математического выражения
 */
public class MathExpressionRootNode extends AbstractNode {

    public Expr getExpr() {
        return expr;
    }

    private enum Expect {
        VALUE(EnumSet.of(VARIABLE, INTEGER, REAL)),
        UNARY(EnumSet.of(PLUS, MINUS)),
        BINARY(EnumSet.of(PLUS, MINUS, STAR_MULTIPLY, DIVIDE, DIV, MOD)),
        FUNCTION(EnumSet.of(TokenType.FUNCTION)),
        OPENING(EnumSet.of(LB)),
        CLOSING(EnumSet.of(RB));

        private final Set<TokenType> tokenTypes;

        Expect(Set<TokenType> tokenTypes) {
            this.tokenTypes = tokenTypes;
        }

        public boolean check(Token token) {
            return tokenTypes.contains(token.getTokenType());
        }

        public static Expect selectFromExpected(Set<Expect> expected, Token token) {
            for (Expect expect : expected) {
                if (expect.check(token)) {
                    return expect;
                }
            }
            return null;
        }
    }

    public static final EnumSet<Expect> INITIAL_EXPECTED = EnumSet.of(Expect.VALUE, Expect.UNARY, Expect.FUNCTION, Expect.OPENING);

    private static final Map<TokenType, Integer> priorities = ImmutableMap.<TokenType, Integer>builder()
            .put(PLUS, 1)
            .put(MINUS, 1)
            .put(STAR_MULTIPLY, 2)
            .put(DIVIDE, 2)
            .put(DIV, 2)
            .put(MOD, 2)
            .build();

    private Expr expr;

    public MathExpressionRootNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        expr = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        Expr expr = parseExpr(lexer);
        if (expr == null) return ParsingResult.FAIL;
        expr.setParent(this);
        ParsingResult parse = expr.parse(lexer);
        if (parse.isOk()) {
            this.expr = expr;
            addChild(expr);
        }
        return parse;
    }

    private Expr parseExpr(Lexer lexer) {
        Set<Expect> expected = INITIAL_EXPECTED;
        Stack<Expr> stack = new Stack<>();
        Token token = null;
        Token prev = null;
        while (!lexer.isEnd()) {
            token = lexer.nextNonIgnored();
            Expect type = Expect.selectFromExpected(expected, token);
            if (type == null) {
                lexer.setPointer(prev);
                break;
            } else {
                Expr peek = null;
                if (!stack.isEmpty()) {
                    peek = stack.peek();
                }
                switch (type) {
                    case FUNCTION:
                        FunctionExpr functionExpr = new FunctionExpr();
                        functionExpr.setName(token);
                        stack.push(new Expr(functionExpr));
                        addToPeekExpr(peek, stack.peek());
                        expected = EnumSet.of(Expect.OPENING);
                        break;
                    case OPENING:
                        if (!stack.isEmpty() && stack.peek().isFunctionExpr() && stack.peek().getFunctionExpr().getLb() == null) {
                            stack.peek().getFunctionExpr().setLb(token);
                        } else {
                            BracketedExpr bracketedExpr = new BracketedExpr();
                            bracketedExpr.setLb(token);
                            Expr expr = new Expr(bracketedExpr);
                            stack.push(expr);
                            addToPeekExpr(peek, stack.peek());
                        }
                        expected = INITIAL_EXPECTED;
                        break;
                    case UNARY:
                        UnaryExprNode unaryExprNode = new UnaryExprNode();
                        unaryExprNode.setOperator(token);
                        Expr unaryExpr = new Expr(unaryExprNode);
                        stack.push(unaryExpr);
                        addToPeekExpr(peek, stack.peek());
                        expected = INITIAL_EXPECTED;
                        break;
                    case VALUE:
                        Expr value = new Expr(token);
                        if (stack.isEmpty()) {
                            stack.push(value);
                        } else {
                            if (stack.peek().isFunctionExpr()) {
                                stack.peek().getFunctionExpr().setExpr(value);
                                stack.push(value);
                            } else if (stack.peek().isBracketedExpr()) {
                                stack.peek().getBracketedExpr().setExpr(value);
                                stack.push(value);
                            } else if (stack.peek().isBinaryExpr()) {
                                stack.peek().getBinaryExpr().setRight(value);
                            } else if (stack.peek().isUnaryExpr()) {
                                stack.peek().getUnaryExpr().setExpr(value);
                            }
                        }
                        expected = EnumSet.of(Expect.BINARY);
                        if (stackContainsUnclosedBrackets(stack)) {
                            expected.add(Expect.CLOSING);
                        }
                        break;
                    case BINARY:
                        if (stack.peek().isBinaryExpr()) {
                            BinaryExprNode binaryExpr = stack.peek().getBinaryExpr();
                            if (priorities.get(binaryExpr.getOperator().getTokenType()) < priorities.get(token.getTokenType())) {
                                BinaryExprNode newBinaryExprNode = new BinaryExprNode();
                                newBinaryExprNode.setLeft(binaryExpr.getRight());
                                newBinaryExprNode.setOperator(token);
                                Expr newExpr = new Expr(newBinaryExprNode);
                                binaryExpr.setRight(newExpr);
                                stack.push(newExpr);
                            } else {
                                Expr left = binaryExpr.getRight();
                                Expr expr;
                                while (true) {
                                    if (stack.isEmpty()) {
                                        expr = null;
                                        break;
                                    }
                                    expr = stack.peek();
                                    if (expr.isBinaryExpr() && priorities.get(expr.getBinaryExpr().getOperator().getTokenType()) > priorities.get(token.getTokenType())) {
                                        left = stack.pop();
                                    } else {
                                        break;
                                    }
                                }
                                BinaryExprNode newBinaryExprNode = new BinaryExprNode();
                                newBinaryExprNode.setLeft(left);
                                newBinaryExprNode.setOperator(token);
                                Expr newBinaryExpr = new Expr(newBinaryExprNode);
                                if (expr != null) {
                                    if (expr.isBracketedExpr()) {
                                        expr.getBracketedExpr().setExpr(newBinaryExpr);
                                    } else if (expr.isFunctionExpr()) {
                                        expr.getFunctionExpr().setExpr(newBinaryExpr);
                                    } else if (expr.isBinaryExpr()) {
                                        expr.getBinaryExpr().setRight(newBinaryExpr);
                                    }
                                }
                                stack.push(newBinaryExpr);
                            }
                        } else {
                            BinaryExprNode binaryExpr = new BinaryExprNode();
                            binaryExpr.setLeft(stack.pop());
                            binaryExpr.setOperator(token);
                            peek = stack.isEmpty() ? null : stack.peek();
                            stack.push(new Expr(binaryExpr));
                            addToPeekExpr(peek, stack.peek());
                        }
                        expected = INITIAL_EXPECTED;
                        if (stackContainsUnclosedBrackets(stack)) {
                            expected.add(Expect.CLOSING);
                        }
                        break;
                    case CLOSING:
                        Expr expr;
                        while (true) {
                            expr = stack.pop();
                            if (expr.isBracketedExpr() && expr.getBracketedExpr().getRb() == null) {
                                expr.getBracketedExpr().setRb(token);
                                break;
                            } else if (expr.isFunctionExpr() && expr.getFunctionExpr().getRb() == null) {
                                expr.getFunctionExpr().setRb(token);
                                break;
                            }
                        }
                        expected = EnumSet.of(Expect.BINARY);
                        if (stackContainsUnclosedBrackets(stack)) {
                            expected.add(Expect.CLOSING);
                        }
                        break;
                }
            }
            prev = token;
        }
        if (token == null) return null;
        return stack.firstElement();
    }

    private boolean stackContainsUnclosedBrackets(Stack<Expr> stack) {
        return stack.stream()
                .filter(Expr::isFunctionExpr)
                .map(Expr::getFunctionExpr)
                .anyMatch(f -> f.getRb() == null)
                || stack.stream()
                .filter(Expr::isBracketedExpr)
                .map(Expr::getBracketedExpr)
                .anyMatch(b -> b.getRb() == null);
    }

    private void addToPeekExpr(Expr peek, Expr expr) {
        if (peek == null) return;
        if (peek.isUnaryExpr()) peek.getUnaryExpr().setExpr(expr);
        if (peek.isFunctionExpr()) peek.getFunctionExpr().setExpr(expr);
        if (peek.isBracketedExpr()) peek.getBracketedExpr().setExpr(expr);
        if (peek.isBinaryExpr()) peek.getBinaryExpr().setRight(expr);
    }

    public Collection<Token> getAllVariables() {
        return expr.getAllVariables();
    }
}
