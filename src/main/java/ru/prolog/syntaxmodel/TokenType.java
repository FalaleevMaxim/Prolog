package ru.prolog.syntaxmodel;

import ru.prolog.syntaxmodel.tree.recognizers.tokens.TokenRecognizer;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.*;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.keywords.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.prolog.syntaxmodel.TokenKind.*;

/**
 * Перечисление типов токенов
 */
public enum TokenType {
    WHITESPACE(IGNORED, new WhitespaceRecognizer()),
    SINGLE_COMMENT(IGNORED, new SingleLineCommentRecognizer()),
    INTEGER(SEMANTIC, new IntegerRecognizer()),
    REAL(SEMANTIC, new RealRecognizer()),
    STRING(SEMANTIC, new StringRecognizer()),
    CHAR(SEMANTIC, new CharRecognizer()),
    SYMBOL(SEMANTIC, new SymbolRecognizer()),
    VARIABLE(SEMANTIC, new VariableRecognizer()),
    ANONYMOUS(SEMANTIC, new AnonymousVariableRecognizer()),
    CUT_SIGN(SEMANTIC, new CutSignRecognizer()),
    DOMAINS_KEYWORD(SYNTAX, new DomainsKeywordRecognizer()),
    DATABASE_KEYWORD(SYNTAX, new DatabaseKeywordRecognizer()),
    PREDICATES_KEYWORD(SYNTAX, new PredicatesKeywordRecognizer()),
    CLAUSES_KEYWORD(SYNTAX, new ClausesKeywordRecognizer()),
    GOAL_KEYWORD(SYNTAX, new GoalKeywordRecognizer()),
    LB(SYNTAX, new LeftBracketRecognizer()),
    RB(SYNTAX, new RightBracketRecognizer()),
    LSQB(SYNTAX, new RightSquareBracketRecognizer()),
    RSQB(SYNTAX, new LeftSquareBracketRecognizer()),
    TAILSEP(SYNTAX, new TailSeparatorRecognizer()),
    COMMA(SYNTAX, new CommaRecognizer()),
    DOT(SYNTAX, new CommaRecognizer()),
    SEMICOLON(SYNTAX, new SemicolonRecognizer()),
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

    TokenType(TokenKind tokenKind, TokenRecognizer recognizer) {
        this.tokenKind = tokenKind;
        this.recognizer = recognizer;
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
        return Arrays.stream(values())
                .map(TokenType::getRecognizer)
                .filter(recognizer -> recognizer instanceof AbstractKeywordRecognizer)
                .map(recognizer -> (AbstractKeywordRecognizer) recognizer)
                .filter(AbstractKeywordRecognizer::isWord)
                .map(AbstractKeywordRecognizer::getKeyword)
                .collect(Collectors.toSet());
    }

    public TokenKind getTokenKind() {
        return tokenKind;
    }

    public TokenRecognizer getRecognizer() {
        return recognizer;
    }
}
