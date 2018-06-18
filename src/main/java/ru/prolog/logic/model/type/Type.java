package ru.prolog.logic.model.type;

import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.AbstractModelObject;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.type.descriptions.CommonType;
import ru.prolog.logic.model.type.descriptions.CompoundType;
import ru.prolog.logic.model.type.exceptions.NoValuesTypeException;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.values.functor.FunctorVariableImpl;
import ru.prolog.logic.values.list.ListVariableImpl;
import ru.prolog.logic.values.simple.SimpleVariable;

import java.util.*;

public final class Type extends AbstractModelObject{
    private PrimitiveType primitiveType;
    private Type listType;
    private String listTypeName;
    private CompoundType compoundType;
    private CommonType commonType;

    public Type(PrimitiveType primitive){
        this.primitiveType = primitive;
    }

    public Type(String listTypeName){
        this.listTypeName = listTypeName;
    }

    public Type(Type listType, String listTypeName){
        this.listType = listType;
        this.listTypeName = listTypeName;
    }

    public Type(CompoundType compoundType){
        this.compoundType = compoundType;
    }

    public Type(CommonType commonType){
        this.commonType = commonType;
    }

    public boolean isList() {
        return listTypeName !=null;
    }

    public Type getListType() {
        return listType;
    }

    public String getListTypeName() {
        return listTypeName;
    }

    public void setListType(Type listType) {
        if(fixed) throw new IllegalStateException("Type is fixed. You can not change it anymore");
        this.listType = listType;
    }

    public boolean isPrimitive(){
        return primitiveType!=null;
    }

    public boolean isBuiltIn(){
        return primitives.containsValue(this);
    }

    public boolean isCommonType(){return commonType!=null;}

    public boolean isAnyType(){
        return isCommonType() && commonType.type== CommonType.Type.ANY;
    }

    public boolean isVarArg(){
        return isCommonType() && commonType.type== CommonType.Type.VARARG;
    }

    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    public boolean isCompoundType(){
        return compoundType !=null;
    }

    public CompoundType getCompoundType() {
        return compoundType;
    }

    public CommonType getCommonType() {
        return commonType;
    }

    public Variable createVariable(String name, RuleContext context){
        if(isPrimitive())
            return new SimpleVariable(this, name, context);
        if(isList())
            return new ListVariableImpl(this, name, context);
        if(isCompoundType())
            return new FunctorVariableImpl(this, name, context);
        throw new NoValuesTypeException(this);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        Collection<ModelStateException> exceptions = new ArrayList<>();
        if(isList() && listType==null)
            exceptions.add(new ModelStateException(this, "List type not set"));
        if(isCompoundType())
            exceptions.addAll(compoundType.exceptions());
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        if(isCompoundType())
            compoundType.fix();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Type)) return false;

        Type type = (Type) o;

        if (primitiveType != null ? !primitiveType.equals(type.primitiveType) : type.primitiveType != null)
            return false;
        if (listTypeName != null ? !listTypeName.equals(type.listTypeName) : type.listTypeName != null) return false;
        if (compoundType != null ? !compoundType.equals(type.compoundType) : type.compoundType != null) return false;
        return commonType != null ? commonType.equals(type.commonType) : type.commonType == null;
    }

    @Override
    public int hashCode() {
        int result = primitiveType != null ? primitiveType.hashCode() : 0;
        result = 31 * result + (listTypeName != null ? listTypeName.hashCode() : 0);
        result = 31 * result + (compoundType != null ? compoundType.hashCode() : 0);
        result = 31 * result + (commonType != null ? commonType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if(isPrimitive())
            return primitiveType.getName();
        if(isList())
            return listTypeName+"*";
        if(isCompoundType())
            return compoundType.toString();
        if(isCommonType()){
            if(commonType.type== CommonType.Type.ANY){
                return "_";
            }else{
                return "...";
            }
        }
        return "<Unknown>";
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

        CommonType commonType = new CommonType(CommonType.Type.ANY);
        pool.put("_", new Type(commonType));

        commonType = new CommonType(CommonType.Type.VARARG);
        pool.put("...", new Type(commonType));

        primitives = Collections.unmodifiableMap(pool);
    }

    public static final class PrimitiveType {
        private final String name;

        private boolean isInteger;
        private boolean isReal;
        private boolean isChar;
        private boolean isString;

        private PrimitiveType(String name, boolean isInteger, boolean isReal, boolean isChar, boolean isString){
            this.name = name;
            this.isInteger = isInteger;
            this.isReal = isReal;
            this.isChar = isChar;
            this.isString = isString;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PrimitiveType)) return false;

            PrimitiveType that = (PrimitiveType) o;

            if (isInteger != that.isInteger) return false;
            if (isReal != that.isReal) return false;
            if (isChar != that.isChar) return false;
            if (isString != that.isString) return false;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (isInteger ? 1 : 0);
            result = 31 * result + (isReal ? 1 : 0);
            result = 31 * result + (isChar ? 1 : 0);
            result = 31 * result + (isString ? 1 : 0);
            return result;
        }
    }
}
