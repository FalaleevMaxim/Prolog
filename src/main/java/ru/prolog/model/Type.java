package ru.prolog.model;

import ru.prolog.model.values.ListValue;
import ru.prolog.model.values.SimpleValue;
import ru.prolog.model.values.Value;
import ru.prolog.model.values.variables.ListVariable;
import ru.prolog.model.values.variables.SimpleVariable;
import ru.prolog.model.values.variables.Variable;

import java.util.HashMap;
import java.util.Map;

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


    private static Map<String, Type> typesPool;

    public static Type getType(String name){
        return typesPool.get(name);
    }

    public static Type aliasType(String name, Type related){
        return getType(name, related, false);
    }

    public static Type listType(String name, Type elementsType){
        return getType(name, elementsType, true);
    }

    private static Type getType(String name, Type related, boolean isList){
        Type type = getType(name);
        if(type == null){
            type = new Type();
            type.listType = related;
            saveType(name, type);
            return type;
        }
        if(type.listType != related || type.isList() != isList)
            throw new IllegalArgumentException("Requested type with existing name but different contents");
        return type;
    }
    public static class PrimitiveType {

        private String name;

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

    private static void saveType(String name, Type type){
        typesPool.put(name, type);
    }

}
