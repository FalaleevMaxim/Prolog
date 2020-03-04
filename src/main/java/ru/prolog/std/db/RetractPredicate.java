package ru.prolog.std.db;

import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.rule.FactRule;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.database.Database;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.functor.FunctorValue;
import ru.prolog.util.keys.PredicateKeys;

import java.util.Collections;
import java.util.List;

public class RetractPredicate extends AbstractPredicate{

    private static final String KEY_RULE_COUNT = PredicateKeys.RETRACT_RULE_COUNT;
    private static final String KEY_START_RULE = PredicateKeys.START_WITH_RULE;

    public RetractPredicate(TypeStorage typeStorage) {
        super("retract", Collections.singletonList("database"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        FunctorValue func = (FunctorValue) args.get(0);
        Database db = context.programContext().database();
        DatabasePredicate predicate = db.get(func.getFunctorName());
        List<FactRule> rules = db.getRules(predicate);
        int count = rules.size();
        Integer startWith = (Integer) context.getContextData(KEY_START_RULE);
        if (startWith == null) startWith = 0;
        Integer prev_count = (Integer) context.getContextData(KEY_RULE_COUNT);
        if(prev_count==null) prev_count=0;
        if(count< prev_count)
            startWith-=prev_count-count;
        context.putContextData(KEY_RULE_COUNT, rules.size());
        for(int i=startWith; i<rules.size(); i++){
            FactRule rule = rules.get(i);
            //Execute rule with arguments from functor
            RuleContext ruleContext = context.programContext().program().managers().getRuleManager().context(rule, func.getArgs(), context);
            if(ruleContext.execute()){
                //If rule returned true, remove it from database
                db.retract(rule);
                context.putContextData(KEY_START_RULE, i);
                return PredicateResult.NEXT_RESULT;
            }
        }
        return PredicateResult.FAIL;
    }
}
