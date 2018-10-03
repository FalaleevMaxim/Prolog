package ru.prolog.runtime.values.functor;

import ru.prolog.runtime.values.Value;

import java.util.List;

public interface FunctorValue extends Value {
    String getFunctorName();
    @Override
    default List<Value> getValue(){
        return getArgs();
    }
    List<Value> getArgs();
}
