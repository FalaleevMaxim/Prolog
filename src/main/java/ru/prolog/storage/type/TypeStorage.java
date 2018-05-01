package ru.prolog.storage.type;

import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.type.Type;
import ru.prolog.model.type.descriptions.Functor;
import ru.prolog.model.type.descriptions.FunctorType;
import ru.prolog.storage.database.Database;

import java.util.Collection;

public interface TypeStorage extends ModelObject {
    Type get(String name);
    Collection<String> names(Type type);
    Functor getFunctor(String name);
    void addType(String name, Type type);
    boolean contains(String typeName);
    Type getDatabaseType();
    void addDatabasePredicate(DatabasePredicate predicate);
}