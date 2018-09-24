package ru.prolog.logic.model.rule;

import ru.prolog.logic.etc.exceptions.model.ModelStateException;
import ru.prolog.logic.etc.exceptions.model.rule.MissingRuleArgException;
import ru.prolog.logic.etc.exceptions.model.rule.RedundantRuleArgException;
import ru.prolog.logic.etc.exceptions.model.rule.WrongRuleArgTypeException;
import ru.prolog.logic.model.AbstractModelObject;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.values.ValueModel;
import ru.prolog.logic.runtime.context.rule.RuleContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.util.ToStringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all rules.
 * Implements method unifyArgs.
 */
public abstract class AbstractRule extends AbstractModelObject implements Rule {
    protected List<ValueModel> toUnifyList;
    protected Predicate predicate;

    protected AbstractRule(){
        toUnifyList = new ArrayList<>();
    }

    protected AbstractRule(Predicate predicate, List<ValueModel> toUnifyList) {
        if(toUnifyList == null) toUnifyList = Collections.emptyList();
        this.toUnifyList = new ArrayList<>(toUnifyList);
        this.predicate = predicate;
    }

    protected AbstractRule(List<ValueModel> toUnifyList){
        if(toUnifyList == null) toUnifyList = new ArrayList<>();
        this.toUnifyList = new ArrayList<>(toUnifyList);
    }

    @Override
    public void setPredicate(Predicate predicate) {
        if(fixed) throw new IllegalStateException("Rule state is fixed. You can not change it anymore.");
        this.predicate = predicate;
    }

    @Override
    public Predicate getPredicate() {
        return predicate;
    }

    @Override
    public void addUnifyArg(ValueModel arg) {
        if(fixed) throw new IllegalStateException("Rule state is fixed. You can not change it anymore.");
        toUnifyList.add(arg);
    }

    @Override
    public List<ValueModel> getUnifyArgs(){
        return toUnifyList;
    }

    @Override
    public final boolean run(List<Value> args, RuleContext context) {
        if(!fixed) throw new IllegalStateException("Rule state is not fixed. Call fix() before using it.");
        return unifyArgs(args, context) && body(context);
    }

    @Override
    public boolean body(RuleContext context) {
        if(!fixed) throw new IllegalStateException("Rule state is not fixed. Call fix() before using it.");
        return true;
    }

    /**
     * @param args Arguments sent to getRule from parent context
     * @param context context of executing this getRule
     * @return true if all arguments unification was successful
     */
    @Override
    public final boolean unifyArgs(List<Value> args, RuleContext context){
        if(!fixed) throw new IllegalStateException("Rule state is not fixed. Call fix() before using it.");
        for(int i = 0; i < toUnifyList.size(); i++ ){
            ValueModel toUnify = toUnifyList.get(i);
            Value inContext = toUnify.forContext(context);
            if(!inContext.unify(args.get(i))) return false;
        }
        return true;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();
        if(predicate==null) return exceptions;
        for(int i = 0; i<predicate.getArgTypeNames().size() && i<toUnifyList.size(); i++){
            if(i>=predicate.getArgTypeNames().size()){
                exceptions.add(new RedundantRuleArgException(this, i));
            }else if(i>=toUnifyList.size()){
                exceptions.add(new MissingRuleArgException(this, predicate, i));
            }else {
                Type ruleType = toUnifyList.get(i).getType();
                Type predType = predicate.getTypeStorage().get(predicate.getArgTypeNames().get(i));
                if (!ruleType.equals(predType)) {
                    exceptions.add(new WrongRuleArgTypeException(predicate, this, i));
                }
            }
        }
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        toUnifyList = Collections.unmodifiableList(new ArrayList<>(toUnifyList));
        toUnifyList.forEach(ValueModel::fix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractRule)) return false;

        AbstractRule that = (AbstractRule) o;

        if (!toUnifyList.equals(that.toUnifyList)) return false;
        return predicate != null ? predicate.getName().equals(that.predicate.getName()) : that.predicate == null;
    }

    @Override
    public int hashCode() {
        int result = toUnifyList.hashCode();
        result = 31 * result + (predicate != null ? predicate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if(predicate==null) return "";
        return ToStringUtil.funcToString(predicate.getName(), toUnifyList);
    }
}
