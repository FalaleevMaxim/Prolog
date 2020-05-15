package ru.prolog.syntaxmodel.tree.semantics;

import ru.prolog.syntaxmodel.tree.Node;
import ru.prolog.syntaxmodel.tree.semantics.attributes.AbstractSemanticAttribute;
import ru.prolog.syntaxmodel.tree.semantics.attributes.errors.AbstractSemanticError;
import ru.prolog.syntaxmodel.tree.semantics.attributes.warnings.AbstractSemanticWarning;

import java.util.*;

public class SemanticInfo {
    private final Node selfNode;
    private final Map<Class<? extends AbstractSemanticAttribute>, AbstractSemanticAttribute> attributes = new HashMap<>();
    private List<AbstractSemanticError> errors = new ArrayList<>();
    private List<AbstractSemanticWarning> warnings = new ArrayList<>();

    public SemanticInfo(Node selfNode) {
        this.selfNode = selfNode;
    }

    public void putAttribute(AbstractSemanticAttribute attr) {
        attributes.put(attr.getClass(), attr);
        attr.setSelfNode(selfNode);
        if(attr instanceof AbstractSemanticError) {
            errors.add((AbstractSemanticError) attr);
        }
        if(attr instanceof AbstractSemanticWarning) {
            warnings.add((AbstractSemanticWarning) attr);
        }
    }

    public <T extends AbstractSemanticAttribute> T getAttribute(Class<T> clazz) {
        return (T) attributes.get(clazz);
    }

    public List<AbstractSemanticError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public List<AbstractSemanticWarning> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
}
