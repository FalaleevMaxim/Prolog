package ru.prolog.logic.model.predicate;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.logic.model.rule.StatementExecutorRule;
import ru.prolog.logic.values.Value;

import java.util.*;

public class InnerGoalPredicate extends GoalPredicate{
    private StatementExecutorRule goalRule = new StatementExecutorRule(this, Collections.emptyList());

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        RuleContext goalContext = context.getRuleManager().context(goalRule, Collections.emptyList(), context);
        if(!goalContext.execute()){
            return -1;
        }
        return 0;
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
    public ModelObject fix() {
        super.fix();
        goalRule.fix();
        return this;
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
