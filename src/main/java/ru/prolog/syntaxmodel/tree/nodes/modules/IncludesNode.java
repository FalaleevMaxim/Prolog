package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;
import ru.prolog.syntaxmodel.tree.nodes.IncludeStatementNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

public class IncludesNode extends AbstractNode {
    private Token keyword;
    private final List<IncludeStatementNode> includes = new ArrayList<>();


    public IncludesNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        keyword = null;
        includes.clear();
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        if(!parseChildToken(lexer, t -> {
            keyword = t;}, TokenType.INCLUDE_KEYWORD)) {
            return FAIL;
        }
        while (parseOptional(lexer, this::parseIncludeStatement).isOk());
        return OK;
    }

    private ParsingResult parseIncludeStatement(Lexer lexer) {
       return parseChildNode(new IncludeStatementNode(this), lexer, includes::add);
    }

    public Token getKeyword() {
        return keyword;
    }

    public List<IncludeStatementNode> getIncludes() {
        return Collections.unmodifiableList(includes);
    }
}
