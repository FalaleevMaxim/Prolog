package ru.prolog.logic.storage.type;

import ru.prolog.logic.etc.exceptions.model.ModelStateException;
import ru.prolog.logic.model.AbstractModelObject;
import ru.prolog.logic.model.NameModel;
import ru.prolog.logic.model.predicate.DatabasePredicate;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.type.descriptions.CompoundType;
import ru.prolog.logic.model.type.descriptions.Functor;
import ru.prolog.logic.storage.type.exception.FunctorAlreadyUsedException;
import ru.prolog.logic.storage.type.exception.TypeAlreadyExistsException;

import java.util.*;

public class TypeStorageImpl extends AbstractModelObject implements TypeStorage {
    private Map<String, Type> types = new HashMap<>();
    private Map<Type, List<String>> names = new HashMap<>();
    private Map<String, Functor> functors;
    private Map<String, NameModel> nameCodeMappings = new HashMap<>();
    private Type databaseType = new Type(new CompoundType());

    public TypeStorageImpl() {
        for (Map.Entry<String, Type> e : Type.primitives.entrySet()){
            addType(e.getKey(), e.getValue());
        }
        types.put("database", databaseType);
    }

    public Type get(String name){
        return types.get(name);
    }

    @Override
    public void addType(String name, Type type){
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        if("database".equals(name)) return;
        if(types.containsKey(name)){
            if(!get(name).equals(type))
                throw new TypeAlreadyExistsException(name);
            else return;
        }
        types.put(name, type);
        if(type.isCompoundType()){
            for (Functor func : type.getCompoundType().getFunctors()) {
                addFunctor(func);
            }
        }
        if(!names.containsKey(type)){
            List<String> typeNames = new ArrayList<>();
            typeNames.add(name);
            names.put(type, typeNames);
        }else{
            names.get(type).add(name);
        }
    }

    @Override
    public Set<String> getAllTypeNames() {
        return types.keySet();
    }

    @Override
    public void addTypes(TypeStorage other) {
        for (String name : other.getAllTypeNames()) {
            addType(name, other.get(name));
        }
    }

    private void addFunctor(Functor func) {
        if(functors==null) functors = new HashMap<>();
        if(func.getTypeStorage()==null) func.setTypeStorage(this);
        if(!functors.containsKey(func.getName())){
            functors.put(func.getName(), func);
        }else{
            throw new FunctorAlreadyUsedException(func, functors.get(func.getName()));
        }
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
        addFunctor(predicate);
    }

    @Override
    public void putNameModel(NameModel nameModel) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        if(!contains(nameModel.getName()))
            throw new IllegalArgumentException("Type "+ nameModel.getName() + " does not exist");
        nameCodeMappings.put(nameModel.getName(), nameModel);
    }

    @Override
    public NameModel typeNameModel(String typeName) {
        return nameCodeMappings.get(typeName);
    }

    @Override
    public Collection<String> names(Type type){
        if(!names.containsKey(type)) return Collections.emptyList();
        return Collections.unmodifiableList(names.get(type));
    }

    @Override
    public Functor getFunctor(String name) {
        if(functors==null) return null;
        for (Type type : types.values()){
            if(type.isCompoundType()){
                for (Functor func : type.getCompoundType().getFunctors()){
                    if(func.getTypeStorage()==null) func.setTypeStorage(this);
                    if(!functors.containsKey(func.getName())){
                        functors.put(func.getName(), func);
                    }
                }
            }
        }
        return functors.get(name);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();
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
                exceptions.addAll(type.getCompoundType().exceptions());
            }
        }
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        types.values().forEach(Type::fix);
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
            }
            if(!first) sb.append(" = ").append(t).append("\n");
        }
        return sb.toString();
    }
}
