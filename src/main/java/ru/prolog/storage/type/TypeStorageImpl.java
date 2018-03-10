package ru.prolog.storage.type;

import ru.prolog.model.Type;
import ru.prolog.storage.type.exception.TypeAlreadyExistsException;
import ru.prolog.storage.type.exception.TypeNotExistsException;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeStorageImpl {
    private Map<String, Type> types;

    public Type get(String name){
        return types.get(name);
    }

    public void add(String name, Type type){
        if(types.containsKey(name)){
            throw new TypeAlreadyExistsException(name);
        }
        types.put(name, type);
    }

    public boolean contains(String typeName){
        return types.containsKey(typeName);
    }

    public Type createAlias(String newName, String existingName){
        if(!types.containsKey(existingName))
            throw new TypeNotExistsException(existingName);
        Type type = types.get(existingName);
        add(newName, type);
        return type;
    }

    Collection<Type> getPrimitives(){
        return types.values().stream()
                .filter(Type::isPrimitive)
                .collect(Collectors.toList());
    }

    Type getListOf(Type elementType){
        return types.values().stream()
                .filter(type -> type.getListType()==elementType)
                .findFirst().orElse(null);
    }

    Collection<String> getNames(Type type){
        return types.entrySet().stream()
                .filter(entry -> entry.getValue()==type)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


}
