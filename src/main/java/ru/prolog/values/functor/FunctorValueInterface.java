package ru.prolog.values.functor;

import ru.prolog.values.Value;

import java.util.List;

public interface FunctorValueInterface extends Value {
    String getFunctorName();
    @Override
    default List<Value> getValue(){
        return getSubObjects();
    }
    List<Value> getSubObjects();
}
