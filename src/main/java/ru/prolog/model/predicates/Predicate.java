package ru.prolog.model.predicates;

import ru.prolog.model.Type;
import ru.prolog.model.values.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Predicate {

    public Predicate(PredicateSignature signature){
        if(signature == null) throw new IllegalArgumentException("Signature of predicate can not be null");
        this.signature = signature;
    }

    private PredicateSignature signature;

    PredicateSignature getSignatute(){
        return signature;
    }

    String getName(){
        return signature.getName();
    }

    List<String> getArgTypeNames(){
        return signature.getArgTypes();
    }

    List<Type> getArgTypes(){
        return getArgTypeNames().stream().map(Type::getType).collect(Collectors.toList());
    }

    protected abstract boolean run(List<Value> args);

    class PredicateSignature {
        public String getName() {
            return name;
        }

        private String name;

        private List<String> argTypes;
        public PredicateSignature(String name){
            if(name==null) throw new IllegalArgumentException("Predicate name can not be null");
            this.name = name;
        }

        public List<String> getArgTypes() {
            if(argTypes==null) return Collections.emptyList();
            return new ArrayList<>(argTypes);
        }

        public void addArgument(String argType){
            if(argType==null) throw new IllegalArgumentException("Adding null argument type");
            if(Type.getType(argType)==null)
                throw new IllegalArgumentException("Not found type for name \"" + argType + "\"");
            if(argTypes==null) argTypes = new ArrayList<>();
            argTypes.add(argType);
        }
    }

    class ExecutablePredicate implements Executable<Predicate>{
        private Predicate predicate;
        private List<Value> args;

        public ExecutablePredicate(Predicate predicate, List<Value> args){
            this.predicate = predicate;
            this.args = args;
        }

        @Override
        public boolean execute() {
            return derived().run(args);
        }

        @Override
        public Predicate derived() {
            return predicate;
        }
    }
}
