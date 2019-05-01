package ru.prolog.model.predicate;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.etc.exceptions.model.predicate.PredicateArgTypeNotExistsException;
import ru.prolog.etc.exceptions.model.predicate.VarArgNotLastException;
import ru.prolog.etc.exceptions.runtime.FreeVariablesException;
import ru.prolog.model.AbstractModelObject;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.type.Type;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.util.ToStringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Базовый класс для всех предикатов.
 * Наследникам требуется передать в конструктор имя предиката и, если есть, список типов аргументов и хранилище типов, и реализовать {@link #run(PredicateContext, List)}
 */
public abstract class AbstractPredicate extends AbstractModelObject implements Predicate {
    protected String name;
    protected List<String> argTypes;
    protected TypeStorage typeStorage;

    /**
     * Предикат без аргументов требует только имя
     * @param name Имя предиката
     */
    public AbstractPredicate(String name){
        this.name = name;
        this.argTypes = new ArrayList<>();
    }

    /**
     * Конструктор, принимающий и имя, и список типов аргументов, и хранилище, из которого брать типы по именам.
     *
     * @param name        Имя предиката
     * @param argTypes    Список имён типов аргументов. Должна быть возможность найти типы по этим именам в typeStorage
     * @param typeStorage Хранилище типов
     */
    public AbstractPredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        this.name = name;
        this.argTypes = new ArrayList<>(argTypes);
        this.typeStorage = typeStorage;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public List<String> getArgTypeNames(){
        return argTypes;
    }

    @Override
    public TypeStorage getTypeStorage() {
        return typeStorage;
    }

    @Override
    public List<Type> getArgTypes() {
        if(getTypeStorage()==null){
            if(getArgTypeNames().isEmpty())
                return Collections.emptyList();
            else return null;
        }
        if(getArgTypeNames().isEmpty())
            return Collections.emptyList();
        return getArgTypeNames().stream()
                .map(s -> getTypeStorage().get(s))
                .collect(Collectors.toList());
    }

    @Override
    public int getArity(){
        if(getArgTypes().isEmpty()) return 0;
        if(getArgTypes().get(getArgTypes().size()-1).isVarArg()) return Integer.MAX_VALUE;
        return getArgTypes().size();
    }

    /**
     * Проверяет основные ошибки для предикатов.
     * Проверяет, что типы аргументов есть в {@link #typeStorage}
     * Проверяет, что vararg (если есть) стоит последним в списке аргументов.
     * @return Список ошибок. Список изменяемый, так что его можно дополнять в производных классах.
     */
    @Override
    public Collection<ModelStateException> exceptions() {
        List<ModelStateException> exceptions = new ArrayList<>();
        for (int i = 0; i < argTypes.size(); i++){
            Type type = typeStorage.get(argTypes.get(i));
            if (type == null)
                //Если типа нет в хранилище, добавляется ошибка
                exceptions.add(new PredicateArgTypeNotExistsException(this, argTypes.get(i), i));
            else if (type.isVarArg() && i < argTypes.size() - 1) {
                //VarArg может стоять только последним аргументом
                exceptions.add(new VarArgNotLastException(this, i));
            }
        }
        return exceptions;
    }

    /**
     * Делает список аргументов неизменяемым.
     */
    @Override
    public void fixIfOk() {
        argTypes = Collections.unmodifiableList(new ArrayList<>(argTypes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPredicate)) return false;

        AbstractPredicate that = (AbstractPredicate) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return argTypes != null ? argTypes.equals(that.argTypes) : that.argTypes == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (argTypes != null ? argTypes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringUtil.funcToString(name, argTypes);
    }

    protected static int getRuleNumberToStart(PredicateContext context) {
        Integer startWith = (Integer) context.getContextData(KEY_START_RULE);
        return startWith == null ? 0 : startWith;
    }

    protected static void setSuccessRule(PredicateContext context, int num) {
        context.putContextData(KEY_START_RULE, num);
    }

    /**
     * Проверяет, является ли переданное значение свободной переменной. Может пригодиться при реализации метода {@link #run(PredicateContext, List)}
     *
     * @param arg проверяемое значение
     * @return {@code true} если значение является переменной
     */
    protected static boolean isFreeVariable(Value arg) {
        return arg instanceof Variable && ((Variable) arg).isFree();
    }

    /**
     * Проверяет, содержит ли список аргументов свободные переменные.
     * Бросает исключение {@link FreeVariablesException} если есть хотя бы одна свободная переменная.
     *
     * @param args Аргументы предиката, в которых не должно быть свободных переменных.
     */
    protected void checkFreeVariables(List<Value> args) {
        List<Variable> freeVariables = args.stream()
                .filter(AbstractPredicate::isFreeVariable)
                .map(value -> (Variable) value)
                .collect(Collectors.toList());
        if (!freeVariables.isEmpty()) {
            throw new FreeVariablesException(
                    String.format("Free variables %s in makeWindow predicate",
                            freeVariables.toString()
                                    .replace('[', '(')
                                    .replace(']', ')')),
                    freeVariables);
        }
    }
}
