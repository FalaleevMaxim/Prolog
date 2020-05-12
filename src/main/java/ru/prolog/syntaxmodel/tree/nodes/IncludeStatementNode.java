package ru.prolog.syntaxmodel.tree.nodes;


import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;

import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Bracketed;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;
import ru.prolog.util.ToStringUtil;

import java.util.*;

import static ru.prolog.syntaxmodel.TokenType.*;
import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.FAIL;
import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.OK;

public class IncludeStatementNode extends AbstractNode implements Bracketed, Separated {
    private static final Set<TokenType> FOLLOW_SET = Collections.unmodifiableSet(EnumSet.of(LB, COMMA, RB));
    private static final List<String> INCLUDE_TYPES = Collections.unmodifiableList(Arrays.asList("predicate", "module"));

    /**
     * Слово "predicate" или "module"
     */
    private Token includeType;

    /**
     * Открывающая скобка
     */
    private Token lb;

    /**
     * Строка, содержашая путь к файлу
     */
    private Token path;

    /**
     * Запятая, отделяющая путь от имени класса
     */
    private Token comma;

    /**
     * Строка с именем класса
     */
    private Token className;

    /**
     * Закрывающая скобка
     */
    private Token rb;

    public IncludeStatementNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        includeType = null;
        lb = null;
        path = null;
        comma = null;
        className = null;
        rb = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, SYMBOL)) {
            includeType = token;
            addChild(includeType);
            if(!INCLUDE_TYPES.contains(includeType.getText())) {
                addError(includeType, false, "Expexted: " +
                        INCLUDE_TYPES.toString().replace("[", "").replace("]", ""));
            }
        } else {
            return FAIL;
        }

        parseOptional(lexer, this::parseBrackets);
        return OK;
    }

    private ParsingResult parseBrackets(Lexer lexer) {
        if(!parseOptional(lexer, this::parseOpening).isOk()) {
            addError(includeType, true, "Expected '('");
        }

        if(!parseOptional(lexer, this::parsePath).isOk()) {
            addError(getLastFilledField(), true, "Expected path string");
        }

        if(!parseOptional(lexer, this::parseComma).isOk()) {
            addError(getLastFilledField(), true, "Expected ','");
        }

        if(!parseOptional(lexer, this::parseClassName).isOk()) {
            addError(getLastFilledField(), true, "Expected class name string");
        }

        if(!parseOptional(lexer, this::parseClosing).isOk()) {
            addError(getLastFilledField(), true, "Expected class name string");
        }

        return OK;
    }

    private ParsingResult parseOpening(Lexer lexer) {
        return parseChildToken(lexer, t -> {lb = t; dontExpect(LB);}, LB) ? OK : FAIL;
    }

    private ParsingResult parsePath(Lexer lexer) {
        return parseChildToken(lexer, t -> {path = t;}, STRING) ? OK : FAIL;
    }

    private ParsingResult parseComma(Lexer lexer) {
        return parseChildToken(lexer, t -> {comma = t; dontExpect(LB, COMMA);}, COMMA) ? OK : FAIL;
    }

    private ParsingResult parseClassName(Lexer lexer) {
        return parseChildToken(lexer, t -> {className = t;}, STRING) ? OK : FAIL;
    }

    private ParsingResult parseClosing(Lexer lexer) {
        return parseChildToken(lexer, t -> {rb = t; dontExpect(LB, COMMA, RB);}, RB) ? OK : FAIL;
    }

    public Token getIncludeType() {
        return includeType;
    }

    @Override
    public Token getLb() {
        return lb;
    }

    public Token getPath() {
        return path;
    }

    public String getPathValue() {
        if(path == null) return null;
        return ToStringUtil.stringTokenValue(path.getText());
    }

    public Token getComma() {
        return comma;
    }

    public Token getClassName() {
        return className;
    }

    public String getClassNameValue() {
        if(className == null) return null;
        return ToStringUtil.stringTokenValue(className.getText());
    }

    @Override
    public Token getRb() {
        return rb;
    }

    @Override
    public List<Token> getSeparators() {
        return Collections.singletonList(getComma());
    }

    @Override
    protected Set<TokenType> initialFollowSet() {
        return FOLLOW_SET;
    }

    private Token getLastFilledField() {
        if(rb != null) return rb;
        if(className != null) return className;
        if(comma != null) return comma;
        if(path != null) return path;
        if(lb != null) return lb;
        return includeType;
    }
}
