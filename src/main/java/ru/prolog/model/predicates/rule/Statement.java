package ru.prolog.model.predicates.rule;

import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.values.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Statement {
    private Predicate predicate;
    private List<Value> args;

    public Statement(Predicate predicate, List<Value> args){
        this.predicate = predicate;
        this.args = new ArrayList<>(args);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public List<Value> getArgs() {
        return Collections.unmodifiableList(args);
    }
}
