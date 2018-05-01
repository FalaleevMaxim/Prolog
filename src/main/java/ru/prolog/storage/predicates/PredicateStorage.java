package ru.prolog.storage.predicates;

import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.type.Type;

import java.util.Collection;
import java.util.List;

public interface PredicateStorage extends ModelObject {
    Collection<Predicate> get(String name);
    Predicate get(String name, int arity);
    Predicate getFitting(String name, List<Type> types);
    void addPredicate(Predicate predicate);
}