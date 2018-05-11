package ru.prolog.logic.values.functor;

import ru.prolog.logic.values.Value;

import java.util.List;

public interface FunctorValue extends Value {
    String getFunctorName();
    @Override
    default List<Value> getValue(){
        return getArgs();
    }
    List<Value> getArgs();
}
