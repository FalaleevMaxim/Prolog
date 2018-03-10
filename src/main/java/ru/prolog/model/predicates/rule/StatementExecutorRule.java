package ru.prolog.model.predicates.rule;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.context.predicate.BasePredicateContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatementExecutorRule extends Rule {
    private List<Statement> statements;

    public StatementExecutorRule(Predicate predicate, List<Value> toUnificate, List<Statement> statements) {
        super(predicate, toUnificate);
        this.statements = new ArrayList<>(statements);
    }

    @Override
    protected boolean body(RuleContext context) {
        RuleContext.ExecutedStatements st = context.getStatements();

        while(st.currentStatement< this.statements.size() && st.currentStatement>st.cutIndex){
            BasePredicateContext execution;
            //if backtracked to executed statement, run same context
            if((st.currentStatement-st.cutIndex) == st.executions.size()){
                execution = st.executions.get(st.executions.size()-1);
            }else{
                Statement statement = this.statements.get(st.currentStatement);
                //if statement is cut, remove all executions and save cut index
                if(isCutPredicate(statement.getPredicate())){
                    st.executions.clear();
                    st.cutIndex = st.currentStatement;
                    if(context.getPredicateContext() != null)
                        context.getPredicateContext().cut();
                    st.currentStatement++;
                    continue;
                }
                //create new context of statement
                execution = new BasePredicateContext(statement.getPredicate(),
                        statement.getArgs().stream()
                                .map(value -> value.forContext(context))
                                .collect(Collectors.toList()));
                st.executions.add(execution);
            }
            if(execution.execute()){
                st.currentStatement++;
            }else{
                st.executions.remove(st.executions.size()-1);
                st.currentStatement--;
            }
        }
        return st.currentStatement > st.cutIndex;
    }

    private boolean isCutPredicate(Predicate predicate){
        return predicate.getName().equals("!") || predicate.getName().equals("cut");
    }
}
