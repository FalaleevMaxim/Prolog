package ru.prolog.values.simple;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.exceptions.value.NullValueException;
import ru.prolog.model.exceptions.value.TypeNotFitValueClassException;
import ru.prolog.model.exceptions.value.TypeNotFitValueObjectException;
import ru.prolog.model.type.Type;
import ru.prolog.values.AbstractValue;
import ru.prolog.values.model.ValueModel;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;
import ru.prolog.values.model.VariableModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimpleValue extends AbstractValue implements ValueModel {
    public SimpleValue(Type type, Object value){
        super(type, value);
    }

    @Override
    public boolean unify(Value other) {
        super.unify(other);
        if(other instanceof SimpleVariable && other.getValue()==null){
            return other.unify(this);
        }
        return value.equals(other.getValue());
    }

    @Override
    public Value forContext(RuleContext context) {
        return this;
    }

    @Override
    public List<VariableModel> innerModelVariables() {
        return Collections.emptyList();
    }

    @Override
    public List<Variable> innerFreeVariables() {
        return Collections.emptyList();
    }

    @Override
    public ValueModel toModel() {
        return this;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        Collection<ModelStateException> exceptions = new ArrayList<>();
        if(value==null)
            exceptions.add(new NullValueException(this));
        if(!getType().isPrimitive()){
            exceptions.add(new TypeNotFitValueClassException(this, "Type of SimpleValue must be primitive"));
        }else{
            Type.PrimitiveType primitive = getType().getPrimitiveType();
            if(primitive.isReal() && !(getValue() instanceof Double) && !(getValue() instanceof Float))
                exceptions.add(new TypeNotFitValueObjectException(this, "Value of type 'real' contains niether Double nor Float"));
            if(primitive.isInteger() && !(getValue() instanceof Integer))
                exceptions.add(new TypeNotFitValueObjectException(this, "Value of type 'integer' contains not Integer object"));
            if(primitive.isString() && !(getValue() instanceof String))
                exceptions.add(new TypeNotFitValueObjectException(this, "Value of string type contains not String object"));
            if(primitive.isChar() && !(getValue() instanceof Character))
                exceptions.add(new TypeNotFitValueObjectException(this, "Value of char type contains not Character object"));
        }
        return exceptions;
    }

    @Override
    public ModelObject fix() {
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()){
            throw exceptions.iterator().next();
        }
        return this;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
