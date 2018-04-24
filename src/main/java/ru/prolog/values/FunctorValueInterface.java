package ru.prolog.values;

import java.util.List;

public interface FunctorValueInterface extends Value {
    String getFunctorName();
    @Override
    List<Value> getValue();
}
