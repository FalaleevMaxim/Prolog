package ru.prolog.model.type.descriptions;

import ru.prolog.model.ModelObject;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.type.Type;

import java.util.List;

public interface Functor extends ModelObject {
    CompoundType getCompoundType();
    String getName();
    List<String> getArgTypeNames();
    TypeStorage getTypeStorage();
    void setCompoundType(CompoundType type);
    void setTypeStorage(TypeStorage typeStorage);

    List<Type> getArgTypes();
}