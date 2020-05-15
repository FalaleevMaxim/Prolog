package ru.prolog.syntaxmodel.tree.semantics.attributes;

import ru.prolog.syntaxmodel.tree.Node;
import ru.prolog.syntaxmodel.tree.semantics.SemanticInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Связь к реализациям от объявления
 */
public class ToImplementations extends AbstractSemanticAttribute {
    private final Set<Node> implementations = new HashSet<>();

    public Set<Node> getImplementations() {
        if(implementations.isEmpty()) return Collections.emptySet();
        return Collections.unmodifiableSet(implementations);
    }

    public void addImplementation(Node newUsage) {
        if (implementations.add(newUsage)) {
            SemanticInfo semanticInfo = newUsage.getSemanticInfo();
            ToDeclaration toDeclarationAttr = semanticInfo.getAttribute(ToDeclaration.class);
            if(toDeclarationAttr == null) {
                semanticInfo.putAttribute(new ToDeclaration(getSelfNode()));
            }
        }
    }
}
