package ru.prolog.runtime.values.functor;

import ru.prolog.model.type.Type;
import ru.prolog.model.values.FunctorValueModel;
import ru.prolog.model.values.ValueModel;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.util.ToStringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctorValueImpl implements FunctorValue {
    protected Type type;
    protected String functorName;
    protected List<Value> args;

    public FunctorValueImpl(Type type, String name, List<Value> args) {
        if(!type.isCompoundType()) throw new IllegalArgumentException("Type must be functor");
        this.type = type;
        this.functorName = name;
        this.args = args;
    }

    @Override
    public String getFunctorName() {
        return functorName;
    }

    @Override
    public List<Value> getArgs() {
        return args;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean unify(Value other) {
        if(other instanceof Variable && ((Variable)other).isFree())
            return other.unify(this);
        FunctorValue otherFunc = (FunctorValue) other;
        //compare functor functorName
        if(!otherFunc.getFunctorName().equals(functorName)){
            return false;
        }
        //Unify args
        for (int i = 0; i < otherFunc.getContent().size(); i++) {
            if (!otherFunc.getContent().get(i).unify(this.getContent().get(i)))
                return false;
        }
        return true;
    }

    @Override
    public List<Variable> innerFreeVariables() {
        List<Variable> variables = new ArrayList<>();
        for (Value arg : args){
            variables.addAll(arg.innerFreeVariables());
        }
        return variables;
    }

    @Override
    public ValueModel toModel() {
        return new FunctorValueModel(type, functorName, args.stream()
                .map(Value::toModel)
                .collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return ToStringUtil.funcToString(functorName, args);
    }
}
