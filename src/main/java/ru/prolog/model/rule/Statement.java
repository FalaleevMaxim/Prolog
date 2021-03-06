package ru.prolog.model.rule;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.etc.exceptions.model.statement.MissingStatementArgException;
import ru.prolog.etc.exceptions.model.statement.RedundantStatementArgException;
import ru.prolog.etc.exceptions.model.statement.StatementStateException;
import ru.prolog.etc.exceptions.model.statement.WrongStatementArgTypeException;
import ru.prolog.model.AbstractModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.type.Type;
import ru.prolog.model.values.ValueModel;
import ru.prolog.std.Not;
import ru.prolog.util.ToStringUtil;

import java.util.*;

public class Statement extends AbstractModelObject {
    private Predicate predicate;
    private String predicateName;
    private List<ValueModel> args;

    public Statement(String predicateName) {
        this.predicateName = predicateName;
        args = new ArrayList<>();
    }

    public Statement(Predicate predicate, List<ValueModel> args){
        setPredicate(predicate);
        this.args = new ArrayList<>(args);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public String getPredicateName() {
        return predicateName;
    }

    public List<ValueModel> getArgs() {
        return args;
    }

    public void setPredicate(Predicate predicate) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.predicate = predicate;
        this.predicateName = predicate.getName();
    }

    public void addArg(ValueModel arg){
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        args.add(arg);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        Collection<ModelStateException> exceptions = new ArrayList<>();
        for (ValueModel arg : args) {
            exceptions.addAll(arg.exceptions());
        }
        if(predicate==null) {
            exceptions.add(new StatementStateException(this, "No predicate for statement "+this));
        }
        if(!exceptions.isEmpty() || !predicate.exceptions().isEmpty()) {
            return exceptions;
        }

        boolean vararg = false; //Sets true after vararg type in predicate. Any arguments in statement can follow vararg.
        for(int i = 0; i<predicate.getArgTypeNames().size() || i<args.size(); i++){
            if(vararg) continue;
            if(i>=predicate.getArgTypeNames().size()){
                exceptions.add(new RedundantStatementArgException(this, i));
            }else if(i>=args.size()){
                exceptions.add(new MissingStatementArgException(this, predicate, i));
            }else {
                Type predType = predicate.getTypeStorage().get(predicate.getArgTypeNames().get(i));
                Type statType = args.get(i).getType();
                if(predType.isVarArg())
                    vararg = true;
                if (!vararg && !predType.isAnyType() && !statType.equals(predType)) {
                    exceptions.add(new WrongStatementArgTypeException(predicate, this, i));
                }
            }
        }
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        args = Collections.unmodifiableList(new ArrayList<>(args));
        args.forEach(ValueModel::fix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Statement)) return false;

        Statement statement = (Statement) o;

        if (!predicate.getName().equals(statement.predicate.getName())) return false;
        return args.equals(statement.args);
    }

    @Override
    public int hashCode() {
        int result = predicate.hashCode();
        result = 31 * result + args.hashCode();
        return result;
    }

    @Override
    public String toString() {
        if(Arrays.asList("=",">","<",">=","<=","<>").contains(predicateName)){
            return args.get(0).toString() +predicateName + args.get(1);
        }
        String s = ToStringUtil.funcToString(predicateName, args);
        if(predicate instanceof Not){
            return "not("+s+")";
        }
        return s;
    }
}