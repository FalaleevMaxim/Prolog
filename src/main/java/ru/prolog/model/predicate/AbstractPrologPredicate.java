package ru.prolog.model.predicate;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.etc.exceptions.model.predicate.IllegalArgTypeException;
import ru.prolog.etc.exceptions.model.predicate.IllegalPredicateNameException;
import ru.prolog.model.rule.Rule;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.type.Type;
import ru.prolog.util.NameChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractPrologPredicate extends AbstractPredicate implements PrologPredicate {
    protected List<Rule> rules;

    AbstractPrologPredicate(String name) {
        super(name);
        this.rules = new ArrayList<>();
    }

    AbstractPrologPredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        super(name, argTypes, typeStorage);
        this.rules = new ArrayList<>();
    }

    AbstractPrologPredicate(String name, List<String> argTypes, List<Rule> rules, TypeStorage typeStorage) {
        super(name, argTypes, typeStorage);
        this.rules = new ArrayList<>(rules);
    }

    @Override
    public void setTypeStorage(TypeStorage typeStorage) {
        if (fixed) throw new IllegalStateException("Predicate state is fixed. You can not change it anymore.");
        this.typeStorage = typeStorage;
    }

    @Override
    public List<Rule> getRules() {
        return rules;
    }

    public void addRule(Rule rule) {
        if (fixed) throw new IllegalStateException("Predicate state is fixed. You can not change it anymore.");
        rules.add(rule);
        rule.setPredicate(this);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if (fixed) return Collections.emptyList();
        List<ModelStateException> exceptions = new ArrayList<>();

        //Check if name is correct
        if (!NameChecker.canBePredicateName(name)) {
            exceptions.add(new IllegalPredicateNameException(this));
        }

        Collection<ModelStateException> argTypesExceptions = super.exceptions();
        exceptions.addAll(argTypesExceptions);
        if(!argTypesExceptions.isEmpty()) {
            return exceptions;
        }
        for (int i = 0; i < argTypes.size(); i++) {
            Type type = typeStorage.get(argTypes.get(i));
            if (type.isCommonType())
                exceptions.add(new IllegalArgTypeException(this, type, i, "Common type can not be argument of RuleExecutorPredicate"));
        }
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        super.fixIfOk();
        argTypes = Collections.unmodifiableList(argTypes);
        rules = Collections.unmodifiableList(new ArrayList<>(rules));
        rules.forEach(Rule::fix);
    }
}
