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

        while (parseOptional(lexer, this::parseTypeDef));
        return true;
    }

    private boolean parseTypeDef(Lexer lexer) {
        TypeDefNode typeDefNode = new TypeDefNode(this);
        if (typeDefNode.parse(lexer)) {
            typeDefNodes.add(typeDefNode);
            addChild(typeDefNode);
            return true;
        }
        return false;
    }
}
