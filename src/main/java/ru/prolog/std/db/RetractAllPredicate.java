package ru.prolog.std.db;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.rule.FactRule;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.database.Database;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.runtime.values.functor.FunctorValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RetractAllPredicate extends AbstractPredicate {
    public RetractAllPredicate(TypeStorage typeStorage) {
        super("retractAll", Collections.singletonList("database"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        FunctorValue func = (FunctorValue) args.get(0);
        for(Variable var : func.innerFreeVariables()){
            if(!var.getName().equals("_")){
                throw new FreeVariableException("Free variables are not allowed in \"retractAll\"", var);
            }
        }
        Database db = context.programContext().database();
        DatabasePredicate predicate = db.get(func.getFunctorName());
        List<FactRule> rules = db.getRules(predicate);
        List<FactRule> toRetract = new ArrayList<>();
        for(FactRule rule : rules){
            //Execute rule with arguments from functor
            RuleContext ruleContext = context.programContext().program().managers().getRuleManager().context(rule, func.getArgs(), context);
            if(ruleContext.execute()){
                //If rule returned true, it should be removed from database.
                //It will be removed after loop to avoid ConcurrentModificationException
                toRetract.add(rule);
                //Rule does not rollback itself when it returned true.,
                ruleContext.rollback();
            }
        }
        toRetract.forEach(db::retract);
        return PredicateResult.LAST_RESULT;
    }
}
