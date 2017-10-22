package ru.prolog.model.predicates.predicate;

import ru.prolog.model.predicates.execution.predicate.PredicateExecution;
import ru.prolog.model.values.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Predicate {
    private  String name;
    private  List<String> argTypes;

    public Predicate(String name, List<String> argTypes) {
        this.name = name;
        this.argTypes = Collections.unmodifiableList(new ArrayList<>(argTypes));
    }

    public String getName(){
        return name;
    }

    public List<String> getArgTypes(){
        return argTypes;
    }

    /**
     * What predicate should do.
     * @param args arguments sent ro predicate
     * @param startWith number of rule to start with
     * @return number of rule which returned true or -1 if no rule returned true.
     */
    public abstract int run(PredicateExecution context, List<Value> args, int startWith);
}
