package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import static ru.prolog.syntaxmodel.TokenType.STAR_MULTIPLY;
import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.FAIL;
import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.OK;

/**
 * Тип узла определения типа списка (название типа и '*')
 */
public class ListTypeNode extends AbstractNode {
    private TypeNameNode typeName;
    private Token arraySymbol;

    public ListTypeNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        typeName = null;
        arraySymbol = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        typeName = new TypeNameNode(this);
        if (typeName.parse(lexer).isOk()) {
            addChild(typeName);
        } else {
            return FAIL;
        }

        Token token = lexer.nextNonIgnored();
        if (ofType(token, STAR_MULTIPLY)) {
            arraySymbol = token;
            addChild(arraySymbol);
            return OK;
        }
        return FAIL;
    }

    public TypeNameNode getTypeName() {
        return typeName;
    }

    public Token getArraySymbol() {
        return arraySymbol;
    }
}
