package ru.prolog.model.predicates.execution.predicate;

import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.values.Value;

import java.util.Collections;
import java.util.List;

public class BasePredicateExecution implements PredicateExecution {
    private Predicate predicate;
    private List<Value> args;
    private int startWith = 0;
    private boolean cut = false;

    public Predicate getPredicate() {
        return predicate;
    }

    public List<Value> getArgs() {
        return args;
    }

    @Override
    public void cut() {
        cut = true;
    }

    @Override
    public boolean isCut() {
        return cut;
    }

    public BasePredicateExecution(Predicate predicate, List<Value> args) {
        if(predicate == null) throw new IllegalArgumentException("Can not create executable of null predicate");
        this.predicate = predicate;
        if(args == null) args = Collections.emptyList();
        this.args = args;
    }

    public boolean execute(){
        if(cut) return false;
        int result = predicate.run(this, args, startWith);
        if(result < 0){
            return false;
        }else{
            startWith = result+1;
            return true;
        }
    }
}
