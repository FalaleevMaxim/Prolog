package ru.prolog.syntaxmodel.tree.semantics.attributes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.tree.Token;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Атрибут, хранящий переменные
 */
public abstract class VariablesHolder extends AbstractSemanticAttribute {

    private Map<String, Set<Token>> variables = new HashMap<>();

    public Set<String> getVariableNames() {
        return variables.keySet();
    }

    public Collection<Token> allVariables() {
        return variables.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Set<Token> byName(String name) {
        if(!variables.containsKey(name)) return Collections.emptySet();
        return Collections.unmodifiableSet(variables.get(name));
    }

    public void addVariable(Token variable) {
        if(variable.getTokenType() == TokenType.ANONYMOUS) return;
        if(variable.getTokenType() != TokenType.VARIABLE) throw new IllegalArgumentException("Token is not variable: " + variable);
        String name = variable.getText();
        if (variables.containsKey(name)) {
            variables.get(name).add(variable);
        } else {
            Set<Token> byName = new HashSet<>();
            byName.add(variable);
            variables.put(name, byName);
        }
        variable.getSemanticInfo().putAttribute(new ToVariablesHolder(this));
    }
}
