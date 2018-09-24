package ru.prolog.logic.model.program;

import ru.prolog.logic.etc.exceptions.model.ModelStateException;
import ru.prolog.logic.model.AbstractModelObject;
import ru.prolog.logic.storage.predicates.PredicateStorage;
import ru.prolog.logic.storage.type.TypeStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class ProgramModule extends AbstractModelObject {
    protected TypeStorage typeStorage;
    protected PredicateStorage predicateStorage;

    public final TypeStorage getTypeStorage(){
        return typeStorage;
    }

    public final PredicateStorage getPredicates(){
        return predicateStorage;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        if(typeStorage==null && getPredicates()==null)
            return Collections.singleton(new ModelStateException(this, "Module is empty"));
        Collection<ModelStateException> exceptions = new ArrayList<>();
        if(typeStorage!=null) exceptions.addAll(typeStorage.exceptions());
        if(predicateStorage!=null) exceptions.addAll(predicateStorage.exceptions());
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        if(predicateStorage!=null){
            predicateStorage.all().stream()
                    .filter(p -> !predicateStorage.isBuiltInPredicate(p))
                    .forEach(p -> p.setCodeIntervals(getCodeIntervals()));
            predicateStorage.fix();
        }
        if(typeStorage!=null){
            typeStorage.getAllTypeNames().stream()
                    .map(typeStorage::get)
                    .filter(type -> !type.isBuiltIn())
                    .forEach(type -> type.setCodeIntervals(getCodeIntervals()));
            typeStorage.fix();
        }
    }
}
