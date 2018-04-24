package ru.prolog.model.type;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.ModelObject;
import ru.prolog.model.type.descriptions.CommonType;
import ru.prolog.model.type.descriptions.FunctorType;
import ru.prolog.model.type.descriptions.PrimitiveType;
import ru.prolog.values.variables.ListVariable;
import ru.prolog.values.variables.SimpleVariable;
import ru.prolog.values.variables.Variable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Type implements ModelObject {
    private String name;
    private PrimitiveType primitiveType;
    private Type listType;
    private FunctorType functorType;
    private CommonType commonType;

    private Type(){}
    public Type(PrimitiveType primitive){
        this.name = primitive.getName();
        this.primitiveType = primitive;
    }

    public Type(String alias, PrimitiveType primitive){
        this.name = alias;
        this.primitiveType = primitive;
    }

    public Type(String name, Type listType){
        this.name = name;
        this.listType = listType;
    }

    public Type(String name, FunctorType functorType){
        this.name = name;
        this.functorType = functorType;
    }

    public String getName() {
        return name;
    }

    public boolean isList() {
        return listType !=null;
    }

    public Type getListType() {
        return listType;
    }

    public boolean isPrimitive(){
        return primitiveType!=null;
    }

    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    public boolean isFunctorType(){
        return functorType!=null;
    }

    public FunctorType getFunctorType() {
        return functorType;
    }

    public boolean anyType(){
        return !isList() && !isPrimitive() && !isFunctorType();
    }

    public static final Map<String, Type> primitives;

    static {
        Map<String, Type> pool = new HashMap<>();

        PrimitiveType primitive = new PrimitiveType("integer", true, false, false, false);
        pool.put("integer", new Type(primitive));

        primitive = new PrimitiveType("real", false, true, false, false);
        pool.put("real", new Type(primitive));

        primitive = new PrimitiveType("string", false, false, false, true);
        pool.put("string", new Type(primitive));

        primitive = new PrimitiveType("symbol", false, false, false, true);
        pool.put("symbol", new Type(primitive));

        primitive = new PrimitiveType("char", false, false, true, false);
        pool.put("char", new Type(primitive));

        primitives = Collections.unmodifiableMap(pool);
    }

    public static class TypeBuilder{
        private Type newType = new Type();
        private boolean created = false;

        public TypeBuilder setListType(Type listType){
            checkTypeSet();
            newType.listType = listType;
            return this;
        }

        public TypeBuilder setPrimitiveType(PrimitiveType primitive){
            checkTypeSet();
            newType.primitiveType = primitive;
            return this;
        }

        public Type create(){
            if(!newType.isPrimitive() && !newType.isList())
                throw new IllegalStateException("Type must be primitive or list");
            created = true;
            return newType;
        }

        private void checkTypeSet() {
            if(created) throw new IllegalStateException("Can not modify type after it is created");
            if(newType.isList()) throw new IllegalStateException("List type already set");
            if(newType.isPrimitive()) throw new IllegalStateException("Type is already primitive.");
        }
    }
}
