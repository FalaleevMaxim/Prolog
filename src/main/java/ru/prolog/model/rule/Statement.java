package ru.prolog.model.rule;

import ru.prolog.model.type.exceptions.WrongTypeException;
import ru.prolog.model.ModelBuilder;
import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.type.Type;
import ru.prolog.values.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Statement implements ModelObject {
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

    public static class StatementBuilder implements ModelBuilder<Statement>{
        private final String name;
        private List<Value> args;
        private Predicate predicate;

        public StatementBuilder(String name){
            this.name = name;
        }

        public void addArg(Value arg){
            if(predicate!=null) throw new IllegalStateException("Predicate already set. Can not modify args list anymore.");
            if(args==null) args = new ArrayList<>();
            args.add(arg);
        }

        public String getName() {
            return name;
        }

        public List<Value> getArgs(){
            if(args==null) return Collections.emptyList();
            return Collections.unmodifiableList(args);
        }

        public void setPredicate(Predicate predicate) {
            if(!predicate.getName().equals(name)) throw new IllegalArgumentException("Wrong predicate functorName. Expected: " + name);
            if(args==null) args = Collections.emptyList();
            if(predicate.getArgTypes().size()!=args.size())
                throw new IllegalArgumentException("Arguments counts are different in predicate and in statement." +
                        " In predicate: " + predicate.getArgTypes().size() +
                        " In statement: " + args.size());
            for (int i = 0; i < predicate.getArgTypes().size(); i++) {
                Type predicateArgType = predicate.getTypeStorage().get(predicate.getArgTypes().get(i));
                Type statementArgType = args.get(i).getType();
                if(!predicateArgType.equals(statementArgType)){
                    throw new WrongTypeException("Type of predicate and statement argument do not match.", predicateArgType, statementArgType);
                }
            }
            this.predicate = predicate;
        }

        @Override
        public Statement create() {
            if(predicate==null) throw new IllegalStateException("Predicate not defined.");
            return new Statement(predicate, args!=null?args:Collections.emptyList());
        }
    }
}