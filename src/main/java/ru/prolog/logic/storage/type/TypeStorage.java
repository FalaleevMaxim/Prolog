package ru.prolog.logic.storage.type;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.NameModel;
import ru.prolog.logic.model.predicate.DatabasePredicate;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.type.descriptions.Functor;

import java.util.Collection;

public interface TypeStorage extends ModelObject {
    Type get(String name);
    Collection<String> names(Type type);
    Functor getFunctor(String name);
    void addType(String name, Type type);
    boolean contains(String typeName);
    Type getDatabaseType();
    void addDatabasePredicate(DatabasePredicate predicate);
    void putNameModel(NameModel nameModel);
    NameModel typeNameModel(String typeName);
}