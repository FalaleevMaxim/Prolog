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

public class SimpleValue extends AbstractValue{
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
    public List<Variable> innerFreeVariables() {
        return Collections.emptyList();
    }

    @Override
    public ValueModel toModel() {
        return new SimpleValueModel();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
