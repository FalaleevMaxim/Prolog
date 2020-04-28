package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends AbstractNode {
    private DomainsNode domains;
    private List<DatabaseNode> databases = new ArrayList<>();
    private PredicatesNode predicates;
    private ClausesNode clauses;
    private GoalNode goal;

    public ProgramNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        domains = null;
        databases.clear();
        predicates = null;
        clauses = null;
        goal = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        skipIgnored(lexer);
        parseOptional(lexer, this::parseDomains);

        while (parseOptional(lexer, this::parseDatabase));

        skipIgnored(lexer);
        if (parseOptional(lexer, this::parsePredicates)) {
            if(!parseClauses(lexer)) {
                valid = false;
            }
        }

        parseOptional(lexer, this::parseGoal);
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

    private boolean parseDatabase(Lexer lexer) {
        DatabaseNode databaseNode = new DatabaseNode(this);
        if(databaseNode.parse(lexer)) {
            databases.add(databaseNode);
            addChild(databaseNode);
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

    private boolean parseClauses(Lexer lexer) {
        ClausesNode clausesNode = new ClausesNode(this);
        if(clausesNode.parse(lexer)) {
            clauses = clausesNode;
            addChild(clauses);
            return true;
        }
        return false;
    }

    private boolean parseGoal(Lexer lexer) {
        GoalNode goalNode = new GoalNode(this);
        if(goalNode.parse(lexer)) {
            goal = goalNode;
            addChild(goal);
            return true;
        }
        return false;
    }

    public DomainsNode getDomains() {
        return domains;
    }

    public PredicatesNode getPredicates() {
        return predicates;
    }

    public ClausesNode getClauses() {
        return clauses;
    }
}
