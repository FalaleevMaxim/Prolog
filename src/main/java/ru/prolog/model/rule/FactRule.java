package ru.prolog.model.rule;

import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.exceptions.rule.RuleStateException;
import ru.prolog.model.exceptions.rule.VariableInFactException;
import ru.prolog.model.type.exceptions.WrongTypeException;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.type.Type;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;
import ru.prolog.values.model.ValueModel;
import ru.prolog.values.model.VariableModel;

import java.util.ArrayList;
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
