package ru.prolog.model.values;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.etc.exceptions.model.value.IllegalVariableNameException;
import ru.prolog.etc.exceptions.model.value.TypeNotFitValueClassException;
import ru.prolog.etc.exceptions.model.value.ValueStateException;
import ru.prolog.etc.exceptions.runtime.WrongTypeException;
import ru.prolog.model.AbstractModelObject;
import ru.prolog.model.type.Type;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.util.NameChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class VariableModel extends AbstractModelObject implements ValueModel{
    protected Type type;
    protected String name;

    public VariableModel(String name) {
        this.name = name;
    }

    public VariableModel(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(Type type){
        if(fixed) throw new IllegalStateException("Variable state is fixed. You can not change it anymore.");
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        Collection<ModelStateException> exceptions = new ArrayList<>();
        if(type==null)
            exceptions.add(new ValueStateException(this, "Variable type not defined"));
        else if (type.isCommonType())
            exceptions.add(new TypeNotFitValueClassException(this, "Common type can not be used for values or variables"));
        if(!NameChecker.canBeVariableName(name)) exceptions.add(new IllegalVariableNameException(this, name));
        return exceptions;
    }

    @Override
    public Set<VariableModel> innerVariables() {
        return Collections.singleton(this);
    }

    @Override
    public Value forContext(RuleContext context) {
        if(!fixed) throw new IllegalStateException("State is not fixed. Call fix() method before using model object.");
        if(name.equals("_")) {
            return type.createVariable(name, context);
        }
        Variable inContext = context.getVariable(name);
        if(inContext==null){
            inContext = type.createVariable(name, context);
            context.addVariable(inContext);
            return inContext;
        }
        if(inContext.getType() != type)
            throw new WrongTypeException(
                    "Type of requested variable does not match type of stored variable",
                    type, inContext.getType());
        return inContext;
    }

    @Override
    public String toString() {
        return name;
    }
}
