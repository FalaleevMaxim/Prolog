package ru.prolog.syntaxmodel.tree.semantics.attributes;

import ru.prolog.syntaxmodel.tree.Node;
import ru.prolog.syntaxmodel.tree.semantics.SemanticInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Связь к использованиям
 */
public class ToUsages extends AbstractSemanticAttribute {
    private final Set<Node> usages = new HashSet<>();

    public Set<Node> getUsages() {
        if(usages.isEmpty()) return Collections.emptySet();
        return Collections.unmodifiableSet(usages);
    }

    public void addUsage(Node newUsage) {
        if (usages.add(newUsage)) {
            SemanticInfo semanticInfo = newUsage.getSemanticInfo();
            ToDeclaration toDeclarationAttr = semanticInfo.getAttribute(ToDeclaration.class);
            if(toDeclarationAttr == null) {
                semanticInfo.putAttribute(new ToDeclaration(getSelfNode()));
            }
        }
    }
}
