package ru.prolog.model.values;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.etc.exceptions.model.value.NullValueException;
import ru.prolog.etc.exceptions.model.value.TypeNotFitValueClassException;
import ru.prolog.etc.exceptions.model.value.TypeNotFitValueObjectException;
import ru.prolog.etc.exceptions.model.value.ValueStateException;
import ru.prolog.model.AbstractModelObject;
import ru.prolog.model.type.Type;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.simple.SimpleValue;
import ru.prolog.util.ToStringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class SimpleValueModel extends AbstractModelObject implements ValueModel{
    private Type type;
    private Object value;

    public SimpleValueModel() {
    }

    public SimpleValueModel(Type type) {
        this.type = type;
    }

    public SimpleValueModel(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public void setType(Type type) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.type = type;
    }

    public void setValue(Object value) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.value = value;
    }

    @Override
    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Value forContext(RuleContext context) {
        if(!fixed) throw new IllegalStateException("Value state is not fixed. Call fix() before using it.");
        return new SimpleValue(type, value);
    }

    @Override
    public Set<VariableModel> innerVariables() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();
        if(value==null)
            exceptions.add(new NullValueException(this));
        if(type==null)
            exceptions.add(new ValueStateException(this, "Value type not defined"));
        if(!exceptions.isEmpty())
            return exceptions;

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
    public String toString() {
        return ToStringUtil.simpleToString(type, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleValueModel)) return false;

        SimpleValueModel that = (SimpleValueModel) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
