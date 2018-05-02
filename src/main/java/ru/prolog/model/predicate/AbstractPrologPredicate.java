package ru.prolog.model.predicate;

import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.exceptions.predicate.IllegalArgTypeException;
import ru.prolog.model.exceptions.predicate.IllegalPredicateNameException;
import ru.prolog.model.rule.Rule;
import ru.prolog.model.type.Type;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.util.NameChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractPrologPredicate extends AbstractPredicate implements PrologPredicate {
    protected List<Rule> rules;
    protected boolean fixed = false;

    public void setName(String name){
        if(fixed) throw new IllegalStateException("Predicate state is fixed. You can not change it anymore.");
        this.name = name;
    }

    protected AbstractPrologPredicate(String name) {
        super(name);
        this.rules = new ArrayList<>();
    }

    public AbstractPrologPredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        super(name, argTypes, typeStorage);
        this.rules = new ArrayList<>();
    }

    public AbstractPrologPredicate(String name, List<String> argTypes, List<Rule> rules, TypeStorage typeStorage) {
        super(name, argTypes, typeStorage);
        this.rules = new ArrayList<>(rules);
    }

    @Override
    public void setTypeStorage(TypeStorage typeStorage) {
        if(fixed) throw new IllegalStateException("Predicate state is fixed. You can not change it anymore.");
        this.typeStorage = typeStorage;
    }

    public void addArgType(String type){
        if(fixed) throw new IllegalStateException("Predicate state is fixed. You can not change it anymore.");
        argTypes.add(type);
    }

    @Override
    public List<Rule> getRules() {
        return rules;
    }

    public void addRule(Rule rule){
        if(fixed) throw new IllegalStateException("Predicate state is fixed. You can not change it anymore.");
        rules.add(rule);
        rule.setPredicate(this);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        List<ModelStateException> exceptions = new ArrayList<>();

        //Check if name is correct
        if(!NameChecker.canBePredicateName(name)){
            exceptions.add(new IllegalPredicateNameException(this));
        }

        Collection<ModelStateException> argTypesExceptions = super.exceptions();
        for(int i=0; i<argTypes.size(); i++) {
            Type type = typeStorage.get(argTypes.get(i));
            if (type.isCommonType())
                exceptions.add(new IllegalArgTypeException(this, type, i, "Common type can not be argument of RuleExecutorPredicate"));
        }
        exceptions.addAll(argTypesExceptions);
        //It is no sense to check rules if there are troubles with argument types.
        if(!argTypesExceptions.isEmpty())
            return exceptions;

        for(Rule rule : rules) {
            //If getRule does not have set predicate, just set it.
            if (rule.getPredicate() != this)
                rule.setPredicate(this);
            exceptions.addAll(rule.exceptions());
        }
        return exceptions;
    }

    @Override
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        //If no exceptions, just fix. Otherwise throw first of exceptions.
        if(exceptions.isEmpty()) {
            fixed = true;
            rules = Collections.unmodifiableList(new ArrayList<>(rules));
            rules.forEach(Rule::fix);
        } else {
            throw exceptions.iterator().next();
        }
        return this;
    }
}
