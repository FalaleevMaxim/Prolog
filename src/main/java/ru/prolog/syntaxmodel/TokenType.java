package ru.prolog.syntaxmodel;

import ru.prolog.syntaxmodel.tree.recognizers.tokens.TokenRecognizer;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.prolog.syntaxmodel.TokenKind.*;

public enum TokenType {
    WHITESPACE(IGNORED, new WhitespaceRecognizer()),
    SINGLE_COMMENT(IGNORED, new SingleLineCommentRecognizer()),
    INTEGER(SEMANTIC, new IntegerRecognizer()),
    REAL(SEMANTIC, new RealRecognizer()),
    STRING(SEMANTIC, new StringRecognizer()),
    SYMBOL(SEMANTIC, new SymbolRecognizer()),
    PREDICATES_KEYWORD(SYNTAX, new PredicatesKeywordRecognizer()),
    LB(SYNTAX, new LeftBracketRecognizer()),
    RB(SYNTAX, new RightBracketRecognizer()),
    COMMA(SYNTAX, new CommaRecognizer()),
    SEMICOLON(SYNTAX, new SemicolonRecognizer()),
    IF_SIGN(SYNTAX, new IfSignRecognizer()),
    IF_KEYWORD(SYNTAX, new IfKeywordRecognizer());


    private final TokenKind tokenKind;
    private final TokenRecognizer recognizer;

    TokenType(TokenKind tokenKind, TokenRecognizer recognizer) {
        this.tokenKind = tokenKind;
        this.recognizer = recognizer;
    }

    public static List<TokenType> getTokenTypes(TokenKind kind) {
        return Arrays.stream(values())
                .filter(type -> type.getTokenKind() == kind)
                .collect(Collectors.toList());
    }

    public static Set<String> getKeywords() {
        return Arrays.stream(values())
                .map(TokenType::getRecognizer)
                .filter(recognizer -> recognizer instanceof AbstractKeywordRecognizer)
                .map(recognizer -> (AbstractKeywordRecognizer) recognizer)
                .filter(AbstractKeywordRecognizer::isText)
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
