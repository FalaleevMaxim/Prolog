package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;

import java.util.ArrayList;
import java.util.List;

public class DomainsNode extends AbstractNode {
    private Token domainsKeyword;
    private List<TypeDefNode> typeDefNodes = new ArrayList<>();

    public DomainsNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        domainsKeyword = null;
        typeDefNodes.clear();
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.DOMAINS_KEYWORD)) {
            domainsKeyword = token;
            addChild(domainsKeyword);
        } else return false;

        boolean parsed;
        do {
            TypeDefNode typeDefNode = new TypeDefNode(this);
            parsed = typeDefNode.parse(lexer);
            if (parsed) {
                typeDefNodes.add(typeDefNode);
                addChild(typeDefNode);
            }
        } while (parsed);

        return true;
    }
}
