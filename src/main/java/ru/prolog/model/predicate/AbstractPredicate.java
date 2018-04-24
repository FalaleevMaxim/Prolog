package ru.prolog.model.predicate;

import ru.prolog.storage.type.TypeStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractPredicate implements Predicate {
    protected final String name;
    protected List<String> argTypes;
    protected TypeStorage typeStorage;

    protected AbstractPredicate(String name){
        this.name = name;
        this.argTypes = new ArrayList<>();
    }

    public AbstractPredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        this.name = name;
        this.argTypes = Collections.unmodifiableList(new ArrayList<>(argTypes));
        this.typeStorage = typeStorage;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public List<String> getArgTypes(){
        return argTypes;
    }

    @Override
    public TypeStorage getTypeStorage() {
        return typeStorage;
    }
}
