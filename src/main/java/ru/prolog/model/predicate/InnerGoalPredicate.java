package ru.prolog.model.predicate;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InnerGoalPredicate extends GoalPredicate{
    private StatementExecutorRule goalRule = new StatementExecutorRule(this, Collections.emptyList());

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        //Создание контекста правила-цели
        RuleContext goalContext = context.getRuleManager().context(goalRule, Collections.emptyList(), context);
        //Единственная задача предиката - запустить целевое правило и вернуть результат.
        return goalContext.execute()
                ? PredicateResult.LAST_RESULT
                : PredicateResult.FAIL;
    }

    public StatementExecutorRule getGoalRule() {
        return goalRule;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        Collection<ModelStateException> exceptions = super.exceptions();
        if(!goalRule.getUnifyArgs().isEmpty()){
            throw new ModelStateException(goalRule, "Goal rule must not have any arguments");
        }
        exceptions.addAll(goalRule.exceptions());
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        goalRule.fix();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("goal\n");
        List<List<Statement>> statements = goalRule.getStatements();
        for (int i = 0; i < statements.size(); i++) {
            if(i!=0) sb.append("; ");
            for (int j=0;j<statements.get(i).size();j++){
                if(j!=0) sb.append(", ");
                sb.append(statements.get(i).get(j));
            }
        }
        return sb.append('.').toString();
    }
}
