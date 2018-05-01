package ru.prolog.values.functor;

import ru.prolog.model.type.Type;
import ru.prolog.util.ToStringUtil;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;
import ru.prolog.values.model.FunctorValueModel;
import ru.prolog.values.model.ValueModel;

import java.util.ArrayList;
import java.util.List;

public class FunctorValue implements FunctorValueInterface {
    protected Type type;
    protected String functorName;
    protected List<Value> subObjects;

    public FunctorValue(Type type, String name, List<Value> subObjects) {
        if(!type.isCompoundType()) throw new IllegalArgumentException("Type must be functor");
        this.type = type;
        this.functorName = name;
        this.subObjects = subObjects;
    }

    @Override
    public String getFunctorName() {
        return functorName;
    }

    @Override
    public List<Value> getSubObjects() {
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
    public List<Variable> innerFreeVariables() {
        List<Variable> variables = new ArrayList<>();
        for (Value v : subObjects){
            variables.addAll(v.innerFreeVariables());
        }
        return variables;
    }

    @Override
    public ValueModel toModel() {
        FunctorValueModel model = new FunctorValueModel(type, functorName);
        for(Value obj : subObjects){
            model.addSubObject(obj.toModel());
        }
        return model;
    }

    @Override
    public String toString() {
        return ToStringUtil.funcToString(functorName, subObjects);
    }
}
