package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.TokenKind;
import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import java.util.*;

import static ru.prolog.syntaxmodel.TokenType.*;

public class ProgramNode extends AbstractNode {
    public static final Set<TokenType> FOLLOW_SET = Collections.unmodifiableSet(EnumSet.of(
            INCLUDE_KEYWORD,
            DOMAINS_KEYWORD,
            DATABASE_KEYWORD,
            PREDICATES_KEYWORD,
            CLAUSES_KEYWORD,
            GOAL_KEYWORD));

    private IncludesNode includes;
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
    protected ParsingResult parseInternal(Lexer lexer) {
        Token pointer = lexer.getPointer();
        Token skip = skipUntilFollowSet(lexer);

        lexer.setPointer(pointer);
        Token token = lexer.nextToken();
        while (token!=skip) {
            if(token == null) break;
            addChild(token);
            if(token.getTokenType()!=null && token.getTokenType().getTokenKind()!= TokenKind.IGNORED) {
                addError(token, false, "Unexpected token " + token);
            }
            token = lexer.nextToken();
        }
        if(token == null) return ParsingResult.ok();

        lexer.setPointer(skip.getPrev());
        switch (skip.getTokenType()) {
            case INCLUDE_KEYWORD:
                dontExpect(INCLUDE_KEYWORD);
                return parseFromIncludes(lexer);
            case DOMAINS_KEYWORD:
                dontExpect(DOMAINS_KEYWORD);
                return parseFromDomains(lexer);
            case DATABASE_KEYWORD:
                dontExpect(INCLUDE_KEYWORD, DOMAINS_KEYWORD);
                return parseFromDatabase(lexer);
            case PREDICATES_KEYWORD:
                dontExpect(INCLUDE_KEYWORD, DOMAINS_KEYWORD, DATABASE_KEYWORD, PREDICATES_KEYWORD);
                return parseFromPredicates(lexer);
            case CLAUSES_KEYWORD:
                dontExpect(INCLUDE_KEYWORD, DOMAINS_KEYWORD, DATABASE_KEYWORD, PREDICATES_KEYWORD, CLAUSES_KEYWORD);
                return parseFromClauses(lexer);
            case GOAL_KEYWORD:
                dontExpect();
                return parseFromGoal(lexer);
        }
        return ParsingResult.ok();
    }

    private ParsingResult parseFromIncludes(Lexer lexer) {
        parseOptional(lexer, this::parseIncludes);
        return parseInternal(lexer);
    }

    private ParsingResult parseFromDomains(Lexer lexer) {
        parseOptional(lexer, this::parseDomains);
        return parseInternal(lexer);
    }

    private ParsingResult parseFromDatabase(Lexer lexer) {
        parseOptional(lexer, this::parseDatabase);
        return parseInternal(lexer);
    }

    private ParsingResult parseFromPredicates(Lexer lexer) {
        parseOptional(lexer, this::parsePredicates);
        return parseInternal(lexer);
    }

    private ParsingResult parseFromClauses(Lexer lexer) {
        parseOptional(lexer, this::parseClauses);
        if(clauses != null && predicates == null) addError(clauses, false, "No predicates before clauses");
        return parseInternal(lexer);
    }

    private ParsingResult parseFromGoal(Lexer lexer) {
        parseOptional(lexer, this::parseGoal);
        return parseInternal(lexer);
    }

    @Override
    protected Set<TokenType> initialFollowSet() {
        return FOLLOW_SET;
    }

    private ParsingResult parseIncludes(Lexer lexer) {
        return parseChildNode(new IncludesNode(this), lexer, t->{includes = t;});
    }

    private ParsingResult parseDomains(Lexer lexer) {
        return parseChildNode(new DomainsNode(this), lexer, t->{domains = t;});
    }

    private ParsingResult parseDatabase(Lexer lexer) {
        return parseChildNode(new DatabaseNode(this), lexer, databases::add);
    }

    private ParsingResult parsePredicates(Lexer lexer) {
        return parseChildNode(new PredicatesNode(this), lexer, t->{predicates = t;});
    }

    private ParsingResult parseClauses(Lexer lexer) {
        return parseChildNode(new ClausesNode(this), lexer, t->{clauses = t;});
    }

    private ParsingResult parseGoal(Lexer lexer) {
        return parseChildNode(new GoalNode(this), lexer, t->{goal = t;});
    }

    public DomainsNode getDomains() {
        return domains;
    }

    public List<DatabaseNode> getDatabases() {
        return Collections.unmodifiableList(databases);
    }

    public PredicatesNode getPredicates() {
        return predicates;
    }

    public ClausesNode getClauses() {
        return clauses;
    }

    public GoalNode getGoal() {
        return goal;
    }

    public IncludesNode getIncludes() {
        return includes;
    }
}
