package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;

import static ru.prolog.syntaxmodel.TokenType.STAR_MULTIPLY;

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
    protected boolean parseInternal(Lexer lexer) {
        typeName = new TypeNameNode(this);
        if (typeName.parse(lexer)) {
            addChild(typeName);
        } else {
            return false;
        }

        Token token = lexer.nextNonIgnored();
        if (ofType(token, STAR_MULTIPLY)) {
            arraySymbol = token;
            addChild(arraySymbol);
            return true;
        }
        return false;
    }
}
