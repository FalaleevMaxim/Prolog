package ru.prolog.logic.std.db;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.model.predicate.DatabasePredicate;
import ru.prolog.logic.model.rule.FactRule;
import ru.prolog.logic.storage.database.Database;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.functor.FunctorValue;

import java.util.Collections;
import java.util.List;

public class RetractPredicate extends AbstractPredicate{
    public RetractPredicate(TypeStorage typeStorage) {
        super("retract", Collections.singletonList("database"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        FunctorValue func = (FunctorValue) args.get(0);
        Database db = context.programContext().database();
        DatabasePredicate predicate = db.get(func.getFunctorName());
        List<FactRule> rules = db.getRules(predicate);
        for(int i=startWith; i<rules.size(); i++){
            FactRule rule = rules.get(i);
            //Execute rule with arguments from functor
            RuleContext ruleContext = context.programContext().program().managers().getRuleManager().context(rule, func.getArgs(), context);
            if(ruleContext.execute()){
                //If rule returned true, remove it from database
                db.retract(rule);
                //Return i-1 because current rule is removed, so next rule has number i.
                return i-1;
            }
        }
        return -1;
    }
}
