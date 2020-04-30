package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;
import ru.prolog.syntaxmodel.tree.nodes.TypeDefNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DomainsNode extends AbstractNode {
    private Token domainsKeyword;
    private final List<TypeDefNode> typeDefNodes = new ArrayList<>();

    public DomainsNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        domainsKeyword = null;
        typeDefNodes.clear();
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.DOMAINS_KEYWORD)) {
            domainsKeyword = token;
            addChild(domainsKeyword);
        } else return ParsingResult.fail();

        while (parseOptional(lexer, this::parseTypeDef).isOk()); //ToDo использовать follow-set
        return ParsingResult.ok();
    }

    private ParsingResult parseTypeDef(Lexer lexer) {
        return parseChildNode(new TypeDefNode(this), lexer, typeDefNodes::add);
    }

    public Token getDomainsKeyword() {
        return domainsKeyword;
    }

    public List<TypeDefNode> getTypeDefNodes() {
        return Collections.unmodifiableList(typeDefNodes);
    }
}
