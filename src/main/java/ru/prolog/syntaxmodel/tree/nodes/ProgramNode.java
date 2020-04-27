package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;

public class ProgramNode extends AbstractNode {
    private DomainsNode domains;
    private PredicatesNode predicates;

    public ProgramNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        domains = null;
        predicates = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        skipIgnored(lexer);
        parseOptional(lexer, this::parseDomains);

        skipIgnored(lexer);
        parseOptional(lexer, this::parsePredicates);
        return true;
    }

    private boolean parseDomains(Lexer lexer) {
        DomainsNode domainsNode = new DomainsNode(this);
        if (domainsNode.parse(lexer)) {
            domains = domainsNode;
            addChild(domains);
            return true;
        }
        return false;
    }

    private boolean parsePredicates(Lexer lexer) {
        PredicatesNode predicatesNode = new PredicatesNode(this);
        if (predicatesNode.parse(lexer)) {
            predicates = predicatesNode;
            addChild(predicates);
            return true;
        }
        return false;
    }
}
