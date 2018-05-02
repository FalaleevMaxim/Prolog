package ru.prolog.values.functor;

import ru.prolog.model.type.Type;
import ru.prolog.util.ToStringUtil;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;
import ru.prolog.values.model.FunctorValueModel;
import ru.prolog.values.model.ValueModel;

import java.util.ArrayList;
import java.util.List;

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
        FunctorValue otherFunc = (FunctorValue) other;
        //compare functor functorName
        if(!otherFunc.getFunctorName().equals(functorName)){
            return false;
        }
        //Unify args
        for(int i=0; i<otherFunc.getValue().size(); i++){
            if(!otherFunc.getValue().get(i).unify(this.getValue().get(i)))
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
        FunctorValueModel model = new FunctorValueModel(type, functorName);
        for(Value arg : args){
            model.addSubObject(arg.toModel());
        }
        return model;
    }

    @Override
    public String toString() {
        return ToStringUtil.funcToString(functorName, args);
    }
}
