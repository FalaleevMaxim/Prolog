package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;
import ru.prolog.syntaxmodel.tree.nodes.RuleNode;

import java.util.ArrayList;
import java.util.List;

import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

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
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (!ofType(token, TokenType.CLAUSES_KEYWORD)) return FAIL;
        clausesKeyword = token;
        addChild(token);

        while (parseOptional(lexer, this::parseRule).isOk()) ;
        if (rules.isEmpty()) {
            addError(clausesKeyword, true, "No rules in clauses module");
        }
        return OK;
    }

    private ParsingResult parseRule(Lexer lexer) {
        RuleNode rule = new RuleNode(this);
        return parseChildNode(rule, lexer, rules::add);
    }
}
