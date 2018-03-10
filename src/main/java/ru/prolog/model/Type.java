package ru.prolog.model;

import ru.prolog.values.ListValue;
import ru.prolog.values.SimpleValue;
import ru.prolog.values.Value;
import ru.prolog.values.variables.ListVariable;
import ru.prolog.values.variables.SimpleVariable;
import ru.prolog.values.variables.Variable;

import java.util.HashMap;

public final class Type {
    private PrimitiveType primitiveType;
    private Type listType;

    private Type(){}
    private Type(PrimitiveType primitive){this.primitiveType = primitive;}

    public Type getListType() {
        return listType;
    }

    public boolean isPrimitive(){
        return primitiveType!=null;
    }

    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    public boolean isList() {
        return listType !=null;
    }

    public Value createValue(Object value){
        if(isList())
            return new ListValue(this, new SimpleValue(listType, value));
        return new SimpleValue(this, value);
    }

    public Variable createVariable(String name){
        if(isList())
            return new ListVariable(this, name);
        return new SimpleVariable(this, name);
    }

    public static class PrimitiveType {

        private final String name;

        private boolean isInteger;
        private boolean isReal;
        private boolean isChar;
        private boolean isString;

        PrimitiveType(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean isNumber() {
            return isInteger || isReal;
        }

        public boolean isInteger() {
            return isInteger;
        }

        public boolean isReal() {
            return isReal;
        }

        public boolean isChar() {
            return isChar;
        }
        public boolean isString() {
            return isString;
        }
    }

    static {
        typesPool = new HashMap<>();

        PrimitiveType primitive = new PrimitiveType("integer");
        primitive.isInteger = true;
        saveType("integer", new Type(primitive));

        primitive = new PrimitiveType("real");
        primitive.isReal = true;
        saveType("real", new Type(primitive));

        primitive = new PrimitiveType("string");
        primitive.isString = true;
        saveType("string", new Type(primitive));

        primitive = new PrimitiveType("symbol");
        primitive.isString = true;
        saveType("symbol", new Type(primitive));

        primitive = new PrimitiveType("char");
        primitive.isChar = true;
        saveType("char", new Type(primitive));
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
