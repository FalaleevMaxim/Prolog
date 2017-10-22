package ru.prolog.model.predicates.predicate;

import ru.prolog.model.values.Value;

import java.util.Collections;
import java.util.List;

public class PredicateExecution {
    private Predicate predicate;
    private List<Value> args;
    private int startWith = 0;

    public PredicateExecution(Predicate predicate, List<Value> args) {
        if(predicate == null) throw new IllegalArgumentException("Can not create executable of null predicate");
        this.predicate = predicate;
        if(args == null) args = Collections.emptyList();
        this.args = args;
    }

    public boolean execute(){
        int result = predicate.run(args, startWith);
        if(result < 0){
            return false;
        }else{
            startWith = result+1;
            return true;
        }
    }
}
