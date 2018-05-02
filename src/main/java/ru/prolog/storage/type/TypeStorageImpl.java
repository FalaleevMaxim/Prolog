package ru.prolog.storage.type;

import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.type.Type;
import ru.prolog.model.type.descriptions.CompoundType;
import ru.prolog.model.type.descriptions.Functor;
import ru.prolog.storage.type.exception.FunctorAlreadyUsedException;
import ru.prolog.storage.type.exception.TypeAlreadyExistsException;

import java.util.*;

public class TypeStorageImpl implements TypeStorage {
    private Map<String, Type> types = new HashMap<>();
    private Map<Type, List<String>> names = new HashMap<>();
    private Map<String, Functor> functors;
    private Type databaseType = new Type(new CompoundType());
    private boolean fixed = false;

    public TypeStorageImpl() {
        for (Map.Entry<String, Type> e : Type.primitives.entrySet()){
            addType(e.getKey(), e.getValue());
        }
    }

    public Type get(String name){
        return types.get(name);
    }

    @Override
    public void addType(String name, Type type){
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        if(types.containsKey(name)){
            if(!type.isPrimitive())
                throw new TypeAlreadyExistsException(name);
            else return;
        }
        types.put(name, type);
        if(!names.containsKey(type)){
            List<String> typeNames = new ArrayList<>();
            typeNames.add(name);
            names.put(type, typeNames);
        }
        types.put("database", databaseType);
    }

    @Override
    public boolean contains(String typeName){
        return types.containsKey(typeName);
    }

    @Override
    public Type getDatabaseType() {
        return databaseType;
    }

    @Override
    public void addDatabasePredicate(DatabasePredicate predicate) {
        databaseType.getCompoundType().addFunctor(predicate);
    }

    @Override
    public Collection<String> names(Type type){
        return Collections.unmodifiableList(names.get(type));
    }

    @Override
    public Functor getFunctor(String name) {
        return functors.get(name);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();
        functors = new HashMap<>();
        for (Type type : types.values()){
            if(type.isList() && type.getListType()==null){
                Type listType = types.get(type.getListTypeName());
                if(listType==null) {
                    exceptions.add(new ModelStateException(type,
                            "Type \"" + type.getListTypeName() + "\" declared as list element type does not exist"));
                }
                else type.setListType(listType);
            }
            if(type.isCompoundType()){
                for (Functor func : type.getCompoundType().getFunctors().values()){
                    if(func.getTypeStorage()==null) func.setTypeStorage(this);
                    exceptions.addAll(func.exceptions());
                    if(functors.containsKey(func.getName())){
                        exceptions.add(new FunctorAlreadyUsedException(func, functors.get(func.getName())));
                    }else{
                        functors.put(func.getName(), func);
                    }
                }
            }
        }
        return exceptions;
    }

    @Override
    public ModelObject fix() {
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()){
            throw exceptions.iterator().next();
        }
        fixed = true;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("domains\n");
        for(Map.Entry<Type, List<String>> str : names.entrySet()){
            Type t = str.getKey();
            if(t.isCommonType()) continue;
            boolean first=true;
            for(String name : str.getValue()){
                if(!(t.isPrimitive() && name.equals(t.getPrimitiveType().getName()))){
                    if(!first) sb.append(", ");
                    sb.append(name);
                    if(first) first=false;
                }
                if(!first) sb.append(" = ").append(t).append("\n");
            }
        }
        return sb.toString();
    }
}
