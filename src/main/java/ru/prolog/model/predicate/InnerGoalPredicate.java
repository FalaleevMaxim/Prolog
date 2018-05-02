package ru.prolog.model.predicate;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;

import java.util.*;

public class InnerGoalPredicate extends GoalPredicate{
    private StatementExecutorRule goalRule = new StatementExecutorRule(this, Collections.emptyList());

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        RuleContext goalContext = context.getRuleManager().context(goalRule, Collections.emptyList(), context);
        if(!goalContext.execute()){
            System.out.println("No");
            return -1;
        }else{
            Set<Variable> out = new HashSet<>();
            for(Variable var : goalContext.getVariables()){
                if(!var.isFree()){
                    System.out.println(var.getName()+"="+var.toString());
                }else{
                    if(out.contains(var)) continue;
                    System.out.print(var.getName());
                    for(Variable rel : var.getRelated()){
                        out.add(rel);
                        System.out.print("="+rel.getName());
                    }
                    System.out.println();
                }
            }
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
        List<Statement> statements = goalRule.getStatements();
        if(!statements.isEmpty()){
            sb.append(statements.get(0));
            for (int i=1;i<statements.size();i++){
                sb.append(", ").append(statements.get(i));
            }
        }
        return sb.append('.').toString();
    }
}
