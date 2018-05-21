package ru.prolog.logic.model.rule;

import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.exceptions.rule.RuleStateException;
import ru.prolog.logic.model.exceptions.rule.VariableInFactException;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.values.ValueModel;
import ru.prolog.logic.model.values.VariableModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Rule with no body.
 * All methods already implemented in {@link AbstractRule Rule} class.
 * This class is made to separate facts from rules with body because facts have different purposes and rules do not extend them. For example, only facts are allowed to be put in database.
 */
public class FactRule extends AbstractRule {
    public FactRule() {
    }

    public FactRule(Predicate predicate, List<ValueModel> toUnificateList) {
        super(predicate, toUnificateList);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = super.exceptions();
        if(predicate==null)
            exceptions.add(new RuleStateException(this, "Predicate not set for fact rule"));
        for(ValueModel arg : toUnifyList){
            for(VariableModel var : arg.innerModelVariables()){
                exceptions.add(new VariableInFactException(this, var));
            }
        }
        return exceptions;
    }
}
