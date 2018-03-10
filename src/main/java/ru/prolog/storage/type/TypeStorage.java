package ru.prolog.storage.type;

import ru.prolog.model.Type;
import ru.prolog.storage.Storage;

public interface TypeStorage extends Storage<Type> {
    Type aliasType(String name, Type related);
    Type listType(String name, Type elementsType);
}
