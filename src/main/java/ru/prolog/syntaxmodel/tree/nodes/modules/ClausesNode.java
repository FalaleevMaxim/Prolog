package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.nodes.RuleNode;

import java.util.ArrayList;
import java.util.List;

public class ClausesNode extends AbstractNode {
    private Token clausesKeyword;
    private final List<RuleNode> rules = new ArrayList<>();


    public ClausesNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        clausesKeyword = null;
        rules.clear();
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(token.getTokenType() != TokenType.CLAUSES_KEYWORD) return false;
        clausesKeyword = token;
        addChild(token);

        while (parseOptional(lexer, this::parseRule));
        if(rules.isEmpty()) {
            valid = false;
        }
        return true;
    }

    private boolean parseRule(Lexer lexer) {
        RuleNode rule = new RuleNode(this);
        if(rule.parse(lexer)) {
            rules.add(rule);
            addChild(rule);
            return true;
        }
        return false;
    }
}
