package ru.prolog.model.rule;

import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.exceptions.statement.MissingStatementArgException;
import ru.prolog.model.exceptions.statement.RedundantStatementArgException;
import ru.prolog.model.exceptions.statement.StatementStateException;
import ru.prolog.model.exceptions.statement.WrongStatementArgTypeException;
import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.type.Type;
import ru.prolog.std.Not;
import ru.prolog.util.ToStringUtil;
import ru.prolog.values.model.ValueModel;

import java.util.*;

public class Statement implements ModelObject {
    private Predicate predicate;
    private String predicateName;
    private List<ValueModel> args;
    private boolean fixed = false;

    public Statement(String predicateName) {
        this.predicateName = predicateName;
        args = new ArrayList<>();
    }

    public Statement(Predicate predicate, List<ValueModel> args){
        setPredicate(predicate);
        this.args = args;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public String getPredicateName() {
        return predicateName;
    }

    public List<ValueModel> getArgs() {
        return Collections.unmodifiableList(args);
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
    @SuppressWarnings("Duplicates")
    public Collection<ModelStateException> exceptions() {
        Collection<ModelStateException> exceptions = new ArrayList<>();
        if(predicate==null) {
            exceptions.add(new StatementStateException(this, "Predicate of statement is null"));
            return exceptions;
        }
        boolean vararg = false; //Sets true after vararg type in predicate. Any arguments in statement can follow vararg.
        for(int i = 0; i<predicate.getArgTypeNames().size() && i<args.size(); i++){
            if(!vararg && i>=predicate.getArgTypeNames().size()){
                exceptions.add(new RedundantStatementArgException(this, i));
            }else if(i>=args.size()){
                exceptions.add(new MissingStatementArgException(this, predicate, i));
            }else {
                Type predType = predicate.getTypeStorage().get(predicate.getArgTypeNames().get(i));
                Type statType = args.get(i).getType();
                if(predType.isVarArg())
                    vararg = true;
                if (!vararg && !predType.isAnyType() && statType.equals(predType)) {
                    exceptions.add(new WrongStatementArgTypeException(predicate, this, i));
                }
            }
        }
        return exceptions;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()) throw exceptions.iterator().next();
        fixed = true;
        args = Collections.unmodifiableList(new ArrayList<>(args));
        args.forEach(ValueModel::fix);
        return this;
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
            return args.get(0).toString() + " " + predicateName + " " + args.get(1);
        }
        String s = ToStringUtil.funcToString(predicateName, args);
        if(predicate!=null && predicate instanceof Not){
            return "not("+s+")";
        }
        return s;
    }
}