package ru.prolog.model.storage.type;

import ru.prolog.model.ModelObject;
import ru.prolog.model.NameModel;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.type.Type;
import ru.prolog.model.type.descriptions.Functor;

import java.util.Collection;
import java.util.Set;

public interface TypeStorage extends ModelObject {
    Type get(String name);
    Collection<String> names(Type type);
    Functor getFunctor(String name);
    void addType(String name, Type type);
    Set<String> getAllTypeNames();
    void addTypes(TypeStorage other);
    boolean contains(String typeName);
    Type getDatabaseType();
    void addDatabasePredicate(DatabasePredicate predicate);
    void putNameModel(NameModel nameModel);
    NameModel typeNameModel(String typeName);
}