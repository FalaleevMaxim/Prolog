package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;

public class ProgramNode extends AbstractNode {
    private DomainsNode domainsNode;

    public ProgramNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        domainsNode = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        skipIgnored(lexer);
        DomainsNode domainsNode1 = new DomainsNode(this);
        if (domainsNode1.parse(lexer)) {
            domainsNode = domainsNode1;
            addChild(domainsNode);
        } else {
            return false;
        }
        skipIgnored(lexer);
        return true;
    }
}
