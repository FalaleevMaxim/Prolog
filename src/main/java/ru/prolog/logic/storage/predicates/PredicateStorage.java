package ru.prolog.logic.storage.predicates;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.std.*;
import ru.prolog.logic.std.cast.*;
import ru.prolog.logic.std.compare.*;
import ru.prolog.logic.std.db.*;
import ru.prolog.logic.std.io.*;
import ru.prolog.logic.std.math.DiffIntPredicate;
import ru.prolog.logic.std.math.SumIntPredicate;
import ru.prolog.logic.std.string.*;

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