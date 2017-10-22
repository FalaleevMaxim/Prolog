package ru.prolog.model.predicates.rule;

import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.predicates.predicate.PredicateExecution;
import ru.prolog.model.predicates.rule.execution.RuleExecution;
import ru.prolog.model.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PredicateExecutorRule extends BaseRule {
    private List<Statement> statements;

    public PredicateExecutorRule(Predicate predicate, List<Value> toUnificate, List<Statement> statements) {
        super(predicate, toUnificate);
        this.statements = new ArrayList<>(statements);
    }

    @Override
    public boolean run(RuleExecution context) {
        List<PredicateExecution> executions = new ArrayList<>(statements.size());
        int cutIndex = -1;
        int currentStatement=0;

        while(currentStatement<statements.size() && currentStatement>cutIndex){
            PredicateExecution execution;
            //if backtracked to executed statement, run same execution
            if((currentStatement-cutIndex) == executions.size()){
                execution = executions.get(executions.size()-1);
            }else{
                Statement statement = statements.get(currentStatement);
                //if statement is cut, remove all executions and save cut index
                if(isCutPredicate(statement.getPredicate())){
                    executions.clear();
                    cutIndex = currentStatement;
                    //ToDo send cut to predicate
                    currentStatement++;
                    continue;
                }
                //create new execution of statement
                execution = new PredicateExecution(statement.getPredicate(),
                        statement.getArgs().stream()
                                .map(value -> processVariable(context, value))
                                .collect(Collectors.toList()));
                executions.add(execution);
            }
            if(execution.execute()){
                currentStatement++;
            }else{
                executions.remove(executions.size()-1);
                currentStatement--;
            }
        }
        return currentStatement>cutIndex;
    }

    private boolean isCutPredicate(Predicate predicate){
        return predicate.getName().equals("!") || predicate.getName().equals("cut");
    }
}
