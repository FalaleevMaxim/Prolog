package ru.prolog.model.type.descriptions;

import ru.prolog.model.ModelObject;
import ru.prolog.model.type.Type;
import ru.prolog.storage.type.TypeStorage;

import java.util.List;
import java.util.stream.Collectors;

public interface Functor extends ModelObject {
    String getName();
    List<String> getArgTypeNames();
    TypeStorage getTypeStorage();
    void setTypeStorage(TypeStorage typeStorage);
    void addArgType(String argType);

    List<Type> getArgTypes();
}
