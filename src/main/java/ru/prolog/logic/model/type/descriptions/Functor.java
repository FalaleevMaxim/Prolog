package ru.prolog.logic.model.type.descriptions;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.storage.type.TypeStorage;

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