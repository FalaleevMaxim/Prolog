package ru.prolog.logic.values.model;

import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.exceptions.value.functor.FunctorValueNameException;
import ru.prolog.logic.model.exceptions.value.TypeNotFitValueClassException;
import ru.prolog.logic.model.exceptions.value.ValueStateException;
import ru.prolog.logic.model.exceptions.value.functor.MissingFunctorArgException;
import ru.prolog.logic.model.exceptions.value.functor.RedundantFunctorArgException;
import ru.prolog.logic.model.exceptions.value.functor.WrongFunctorSubObjectTypeException;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.type.descriptions.Functor;
import ru.prolog.logic.util.NameChecker;
import ru.prolog.logic.util.ToStringUtil;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.functor.FunctorValueImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FunctorValueModel implements ValueModel {
    private Type type;
    private String functorName;
    private List<ValueModel> args;
    private boolean fixed = false;

    public FunctorValueModel() {
    }

    public FunctorValueModel(Type type) {
        this.type = type;
    }

    public FunctorValueModel(String functorName) {
        this.functorName = functorName;
    }

    public FunctorValueModel(Type type, String functorName) {
        this.type = type;
        this.functorName = functorName;
    }

    public FunctorValueModel(Type type, String functorName, List<ValueModel> args) {
        this.type = type;
        this.functorName = functorName;
        this.args = args;
    }

    @Override
    public Type getType() {
        return type;
    }

    public String getFunctorName() {
        return functorName;
    }

    public List<ValueModel> getArgs() {
        return args!=null?args:Collections.emptyList();
    }

    public void setType(Type type){
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.type = type;
    }

    public void setFunctorName(String functorName) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.functorName = functorName;
    }

    public void addSubObject(ValueModel obj){
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        if(args==null) args = new ArrayList<>();
        args.add(obj);
    }

    @Override
    public Value forContext(RuleContext context) {
        if(!fixed) throw new IllegalStateException("State is not fixed. Call fix() method before using model object.");
        return new FunctorValueImpl(type, functorName,
                args.stream()
                        .map(v -> v.forContext(context))
                        .collect(Collectors.toList()));
    }

    @Override
    public List<VariableModel> innerModelVariables() {
        List<VariableModel> variables = new ArrayList<>();
        for(ValueModel arg : args){
            variables.addAll(arg.innerModelVariables());
        }
        return variables;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();
        if(functorName==null) {
            exceptions.add(new FunctorValueNameException(this, "Functor name is null"));
        }else if(!NameChecker.canBeFunctorName(functorName)){
            exceptions.add(new FunctorValueNameException(this, "Functor name has wrong format."));
        }

        Functor func = null;
        if(type==null) {
            exceptions.add(new ValueStateException(this, "Type is null"));
        } else{
            if(!type.isCompoundType()){
                exceptions.add(new TypeNotFitValueClassException(this, "Type of FunctorValueModel must be functor"));
            }else{
                //Check if type contains functor with appropriate name
                if(functorName!=null && (func = type.getCompoundType().getFunctor(functorName))==null) {
                    exceptions.add(new FunctorValueNameException(this, "Functor name not found in compound type."));
                }
            }
        }
        if(!exceptions.isEmpty())
            return exceptions;

        for (ValueModel arg : args){
            exceptions.addAll(arg.exceptions());
        }
        if(!exceptions.isEmpty())
            return exceptions;

        for(int i = 0; i<func.getArgTypes().size() && i< args.size(); i++){
            if(i>=func.getArgTypes().size()){
                exceptions.add(new RedundantFunctorArgException(this, i));
            }else if(i>= args.size()){
                exceptions.add(new MissingFunctorArgException(this, func, i));
            }else {
                Type type = args.get(i).getType();
                Type expected = func.getArgTypes().get(i);
                if (!type.equals(expected)) {
                    exceptions.add(new WrongFunctorSubObjectTypeException(func, this, i));
                }
            }
        }
        return exceptions;
    }

    @Override
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()){
            throw exceptions.iterator().next();
        }
        fixed = true;
        args.forEach(ValueModel::fix);
        args = Collections.unmodifiableList(new ArrayList<>(args));
        return this;
    }

    @Override
    public String toString() {
        return ToStringUtil.funcToString(functorName, args);
    }
}
