package ru.prolog.values;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.type.Type;
import ru.prolog.model.type.descriptions.FunctorType;
import ru.prolog.model.type.exceptions.WrongFunctorNameException;
import ru.prolog.model.type.exceptions.WrongFunctorTypesException;
import ru.prolog.values.variables.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctorValue implements FunctorValueInterface {
    protected Type type;
    protected String functorName;
    protected List<Value> subObjects;

    public FunctorValue(Type type, String name, List<Value> subObjects) {
        if(!type.isFunctorType()) throw new IllegalArgumentException("Type must be functor");
        FunctorType.SubFunctor sf = type.getFunctorType().subFunctors
                .stream().filter(sf1 -> sf1.name.equals(name))
                .findFirst().orElseThrow(() -> new WrongFunctorNameException(name, type));
        List<String> argTypes = subObjects.stream().map(Value::getType).map(Type::getName).collect(Collectors.toList());
        if(!argTypes.equals(sf.argTypes)){
            throw new WrongFunctorTypesException(type, sf.argTypes, argTypes);
        }
        this.type = type;
        this.functorName = name;
        this.subObjects = subObjects;
    }

    @Override
    public String getFunctorName() {
        return functorName;
    }

    @Override
    public List<Value> getValue() {
        return subObjects;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean unify(Value other) {
        FunctorValueInterface otherFunc = (FunctorValueInterface) other;
        //compare functor functorName
        if(!otherFunc.getFunctorName().equals(functorName)){
            return false;
        }
        //Unify subObjects
        for(int i=0; i<otherFunc.getValue().size(); i++){
            if(!otherFunc.getValue().get(i).unify(this.getValue().get(i)))
                return false;
        }
        return true;
    }

    @Override
    public Value forContext(RuleContext context) {
        if(subObjects.isEmpty()) return this;
        //Copy self with forContext() for each subObject
        return new FunctorValue(type, functorName, subObjects
                .stream().map(v -> v.forContext(context))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Variable> innerVariables() {
        List<Variable> variables = new ArrayList<>();
        for (Value v : subObjects){
            variables.addAll(v.innerVariables());
        }
        return variables;
    }
}
