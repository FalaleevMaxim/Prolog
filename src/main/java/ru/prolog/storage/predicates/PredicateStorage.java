package ru.prolog.storage.predicates;

import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.type.Type;
import ru.prolog.std.Cut;
import ru.prolog.std.Cut2;
import ru.prolog.std.EqualsOperatorPredicate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface PredicateStorage extends ModelObject {
    Collection<Predicate> all();
    Collection<Predicate> get(String name);
    Predicate get(String name, int arity);
    Predicate getVarArgPredicate(String name);
    Predicate getFitting(String name, List<Type> types);
    void add(Predicate predicate);
}