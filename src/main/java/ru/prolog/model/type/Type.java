package ru.prolog.model.type;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.AbstractModelObject;
import ru.prolog.model.ModelObject;
import ru.prolog.model.type.descriptions.CommonType;
import ru.prolog.model.type.descriptions.CompoundType;
import ru.prolog.model.type.exceptions.NoValuesTypeException;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Variable;
import ru.prolog.runtime.values.functor.FunctorVariableImpl;
import ru.prolog.runtime.values.list.ListVariableImpl;
import ru.prolog.runtime.values.simple.SimpleVariable;

import java.util.*;

/**
 * Тип данных
 */
public final class Type extends AbstractModelObject{
    /**
     * Описание примитивного типа
     */
    private PrimitiveType primitiveType;
    /**
     * Тип элемента списка
     */
    private Type listType;
    /**
     * Имя типа элемента списка
     */
    private String listTypeName;
    /**
     * Описание составного типа
     */
    private CompoundType compoundType;
    /**
     * Описание обобщённого типа
     */
    private CommonType commonType;

    /**
     * Конструктор примитивного типа.
     * Конструктор скрыт чтобы примитивы можно было только брать из пула {@link #primitives}, а не создавать.
     *
     * @param primitive Описание примитивного типа
     */
    private Type(PrimitiveType primitive) {
        this.primitiveType = primitive;
        fix();
    }

    /**
     * Конструктор списочного типа
     * @param listTypeName Имя типа элемента списка
     */
    public Type(String listTypeName){
        this.listTypeName = listTypeName;
    }

    /**
     * Конструктор списочного типа
     * @param listType Тип элемента списка
     * @param listTypeName Имя типа элемента списка
     */
    public Type(Type listType, String listTypeName){
        this.listType = listType;
        this.listTypeName = listTypeName;
    }

    /**
     * Конструктор составного типа
     * @param compoundType Описание составного типа
     */
    public Type(CompoundType compoundType){
        this.compoundType = compoundType;
    }

    /**
     * Конструктор обобщённого типа
     * @param commonType Описание обобщённого типа
     */
    public Type(CommonType commonType){
        this.commonType = commonType;
    }

    /**
     * Является ли тип списочным
     */
    public boolean isList() {
        return listTypeName !=null;
    }

    /**
     * Возвращает тип элемента списка
     * @return Тип элемента списка или {@code null} если тип не списочный или тип списка ещё не задан.
     */
    public Type getListType() {
        return listType;
    }

    /**
     * Возвращает имя типа элемента списка
     * @return Имя типа элемента списка или {@code null} если тип не списочный.
     */
    public String getListTypeName() {
        return listTypeName;
    }

    /**
     * Устанавливает тип элемента списка.
     * @throws IllegalStateException Если тип не списочный.
     * @param listType Тип элемента списка.
     */
    public void setListType(Type listType) {
        if(fixed) throw new IllegalStateException("Type is fixed. You can not change it anymore");
        if (!isList()) throw new IllegalStateException("Type is not list. Can not set list element type");
        this.listType = listType;
    }

    /**
     * Является ли тип примитивным
     */
    public boolean isPrimitive(){
        return primitiveType!=null;
    }

    /**
     * Является ли тип встроенным в язык
     */
    public boolean isBuiltIn(){
        return primitives.containsValue(this);
    }

    /**
     * Является ли тип обобщённым
     */
    public boolean isCommonType(){return commonType!=null;
    }

    /**
     * Обозначает ли тип один аргумент произвольного типа
     */
    public boolean isAnyType() {
        return isCommonType() && commonType.kind == CommonType.CommonTypeKind.ANY;
    }

    /**
     * Является ли тип vararg (обозначает один или более аргумент произвольного типа)
     */
    public boolean isVarArg() {
        return isCommonType() && commonType.kind == CommonType.CommonTypeKind.VARARG;
    }

    /**
     * Возвразает описание примитивного типа
     */
    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    /**
     * Возвразает описание обобщённого типа
     */
    public CommonType getCommonType() {
        return commonType;
    }

    /**
     * Является ли тип составным
     */
    public boolean isCompoundType(){
        return compoundType !=null;
    }

    /**
     * Возвразает описание составного типа
     */
    public CompoundType getCompoundType() {
        return compoundType;
    }

    /**
     * Создаёт объект переменной этого типа. Выбирает класс переменной в зависимости от категории типа (примитивный, списочный или составной).
     * Не регистрируе переменную в контексте!
     * @param name Имя переменной
     * @param context Контекст правила, которому принадлежит переменная
     * @throws NoValuesTypeException Если тип обобщённый.
     * @return Объект переменной данного типа.
     */
    public Variable createVariable(String name, RuleContext context) {
        if (!fixed)
            throw new IllegalStateException("Type not fixed");
        if(isPrimitive())
            return new SimpleVariable(this, name, context);
        if(isList())
            return new ListVariableImpl(this, name, context);
        if(isCompoundType())
            return new FunctorVariableImpl(this, name, context);
        throw new NoValuesTypeException(this);
    }

    @Override
    public Type fix() {
        return (Type) super.fix();
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
        if(isCommonType()) {
            if (commonType.kind == CommonType.CommonTypeKind.ANY){
                return "_";
            }else{
                return "...";
            }
        }
        return "<Unknown>";
    }

    /**
     * Встроенные примитивные типы
     */
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

        CommonType commonType = new CommonType(CommonType.CommonTypeKind.ANY);
        pool.put("_", new Type(commonType));

        commonType = new CommonType(CommonType.CommonTypeKind.VARARG);
        pool.put("...", new Type(commonType));

        primitives = Collections.unmodifiableMap(pool);
    }

    /**
     * Описание примитивного типа
     */
    public static final class PrimitiveType {
        /**
         * Имя примитива
         */
        private final String name;
        /**
         * Является целым числом
         */
        private boolean isInteger;
        /**
         * Является вещественным числом
         */
        private boolean isReal;
        /**
         * Является символом
         */
        private boolean isChar;
        /**
         * Является строкой
         */
        private boolean isString;

        /**
         * Конструктор закрыт чтобы примитивы можно было создавать только внутри класса Type.
         * @param name Имя примитива
         * @param isInteger Является целым числом
         * @param isReal Является вещественным числом
         * @param isChar Является символом
         * @param isString Является строкой
         */
        private PrimitiveType(String name, boolean isInteger, boolean isReal, boolean isChar, boolean isString){
            this.name = name;
            this.isInteger = isInteger;
            this.isReal = isReal;
            this.isChar = isChar;
            this.isString = isString;

        }

        /**
         * Возвращает имя примитива
         */
        public String getName() {
            return name;
        }

        /**
         * Является числом
         */
        public boolean isNumber() {
            return isInteger || isReal;
        }

        /**
         * Является целым числом
         */
        public boolean isInteger() {
            return isInteger;
        }

        /**
         * Является вещественным числом
         */
        public boolean isReal() {
            return isReal;
        }

        /**
         * Является символом
         */
        public boolean isChar() {
            return isChar;
        }

        /**
         * Является строкой
         */
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
