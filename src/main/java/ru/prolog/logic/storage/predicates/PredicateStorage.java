package ru.prolog.logic.storage.predicates;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.type.Type;

import java.util.Collection;
import java.util.List;

public interface PredicateStorage extends ModelObject {
    Collection<Predicate> all();
    Collection<Predicate> get(String name);
    Predicate get(String name, int arity);
    Predicate getVarArgPredicate(String name);
    Predicate getFitting(String name, List<Type> types);
    void add(Predicate predicate);
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isBuiltInPredicate(Predicate p);
}