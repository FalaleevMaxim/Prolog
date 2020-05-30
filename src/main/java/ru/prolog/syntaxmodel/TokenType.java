package ru.prolog.syntaxmodel;

import ru.prolog.syntaxmodel.tree.recognizers.tokens.*;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.keywords.*;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.math.*;

import java.util.*;
import java.util.stream.Collectors;

import static ru.prolog.syntaxmodel.TokenKind.*;

/**
 * Перечисление типов токенов
 */
public enum TokenType {
    WHITESPACE(IGNORED, new WhitespaceRecognizer()),
    SINGLE_COMMENT(IGNORED, new SingleLineCommentRecognizer()),
    MULTILINE_COMMENT(IGNORED, new MultiLineCommentRecognizer()),
    INTEGER(SEMANTIC, new IntegerRecognizer()),
    REAL(SEMANTIC, new RealRecognizer()),
    STRING(SEMANTIC, new StringRecognizer()),
    CHAR(SEMANTIC, new CharRecognizer()),
    PRIMITIVE(SEMANTIC, new PrimitiveTypeRecognizer()),
    SYMBOL(SEMANTIC, new SymbolRecognizer()),
    VARIABLE(SEMANTIC, new VariableRecognizer()),
    ANONYMOUS(SEMANTIC, new AnonymousVariableRecognizer()),
    CUT_SIGN(SEMANTIC, new CutSignRecognizer()),
    INCLUDE_KEYWORD(SYNTAX, new IncludeKeywordRecognizer()),
    DOMAINS_KEYWORD(SYNTAX, new DomainsKeywordRecognizer()),
    DATABASE_KEYWORD(SYNTAX, new DatabaseKeywordRecognizer()),
    PREDICATES_KEYWORD(SYNTAX, new PredicatesKeywordRecognizer()),
    CLAUSES_KEYWORD(SYNTAX, new ClausesKeywordRecognizer()),
    GOAL_KEYWORD(SYNTAX, new GoalKeywordRecognizer()),
    STAR_MULTIPLY(SYNTAX, new StarOrMultiplyCharacterRecognizer()),
    FUNCTION(SEMANTIC, new MathFunctionNameRecognizer()),
    PLUS(SYNTAX, new PlusRecognizer()),
    MINUS(SYNTAX, new MinusRecognizer()),
    DIVIDE(SYNTAX, new DivideRecognizer()),
    DIV(SYNTAX, new DivRecognizer()),
    MOD(SYNTAX, new ModRecognizer()),
    EQUALS(SYNTAX, new EqualSignRecognizer()),
    GREATER(SYNTAX, new GreaterSignRecognizer()),
    LESSER(SYNTAX, new LesserSignRecognizer()),
    GREATER_EQUALS(SYNTAX, new GreaterEqualsSignRecognizer()),
    LESSER_EQUALS(SYNTAX, new LesserEqualsSignRecognizer()),
    NOT_EQUALS(SYNTAX, new NotEqualsSignRecognizer()),
    LB(SYNTAX, new LeftBracketRecognizer()),
    RB(SYNTAX, new RightBracketRecognizer()),
    LSQB(SYNTAX, new LeftSquareBracketRecognizer()),
    RSQB(SYNTAX, new RightSquareBracketRecognizer()),
    TAILSEP(SYNTAX, new TailSeparatorRecognizer()),
    COMMA(SYNTAX, new CommaRecognizer()),
    DOT(SYNTAX, new DotRecognizer()),
    SEMICOLON(SYNTAX, new SemicolonRecognizer()),
    AND_KEYWORD(SYNTAX, new AndKeywordRecognizer()),
    OR_KEYWORD(SYNTAX, new OrKeywordRecognizer()),
    IF_SIGN(SYNTAX, new IfSignRecognizer()),
    IF_KEYWORD(SYNTAX, new IfKeywordRecognizer());

    /**
     * Категория токена
     */
    private final TokenKind tokenKind;
    /**
     * Распознаватель токена
     */
    private final TokenRecognizer recognizer;

    /**
     * Типы заголовков модулей программы
     */
    public static final Set<TokenType> HEADERS = Collections.unmodifiableSet(EnumSet.of(
            TokenType.DOMAINS_KEYWORD,
            TokenType.DATABASE_KEYWORD,
            TokenType.PREDICATES_KEYWORD,
            TokenType.CLAUSES_KEYWORD,
            TokenType.GOAL_KEYWORD));

    TokenType(TokenKind tokenKind, TokenRecognizer recognizer) {
        this.tokenKind = tokenKind;
        this.recognizer = recognizer;
        recognizer.setTokenType(this);
    }

    /**
     * Возвращает все типы токенов заданной категории
     *
     * @param kind Категория токенов.
     * @return Список типов токенов, относящихся к заданной категории.
     */
    public static List<TokenType> getTokenTypes(TokenKind kind) {
        return Arrays.stream(values())
                .filter(type -> type.getTokenKind() == kind)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает все ключевые слова.
     * Ключевыми словами считаются типы токенов, распознаватели которых распознают конкретные слова.
     *
     * @return Список всех ключевых слов
     */
    public static Set<String> getKeywords() {
        Set<String> keywords = Arrays.stream(values())
                .map(TokenType::getRecognizer)
                .filter(recognizer -> recognizer instanceof AbstractKeywordRecognizer)
                .map(recognizer -> (AbstractKeywordRecognizer) recognizer)
                .filter(AbstractKeywordRecognizer::isWord)
                .map(AbstractKeywordRecognizer::getKeyword)
                .collect(Collectors.toSet());
        keywords.addAll(PrimitiveTypeRecognizer.PRIMITIVES);
        return keywords;
    }

    public TokenKind getTokenKind() {
        return tokenKind;
    }

    public TokenRecognizer getRecognizer() {
        return recognizer;
    }
}
