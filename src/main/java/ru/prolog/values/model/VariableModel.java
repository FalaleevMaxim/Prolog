package ru.prolog.values.model;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.exceptions.value.IllegalVariableNameException;
import ru.prolog.model.exceptions.value.TypeNotFitValueClassException;
import ru.prolog.model.exceptions.value.ValueStateException;
import ru.prolog.model.type.Type;
import ru.prolog.model.type.exceptions.WrongTypeException;
import ru.prolog.util.NameChecker;
import ru.prolog.values.AnonymousVariable;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class VariableModel implements ValueModel{
    protected Type type;
    protected String name;
    protected boolean fixed = false;

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
    public ModelObject fix() {
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()){
            throw exceptions.iterator().next();
        }
        fixed = true;
        return this;
    }

    @Override
    public List<VariableModel> innerModelVariables() {
        return Collections.singletonList(this);
    }

    @Override
    public Value forContext(RuleContext context) {
        if(!fixed) throw new IllegalStateException("State is not fixed. Call fix() method before using model object.");
        if(name.equals("_"))
            return new AnonymousVariable(type);
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
