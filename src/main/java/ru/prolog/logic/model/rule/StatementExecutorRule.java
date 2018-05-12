package ru.prolog.logic.model.rule;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.rule.statements.ExecutedStatements;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.context.rule.statements.ExecutedStatement;
import ru.prolog.logic.managers.Managers;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.model.ValueModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StatementExecutorRule extends AbstractRule {
    private List<Statement> statements;

    public StatementExecutorRule() {
        statements = new ArrayList<>();
    }

    public StatementExecutorRule(Predicate predicate){
        setPredicate(predicate);
        this.statements = new ArrayList<>();
    }

    public StatementExecutorRule(List<ValueModel> toUnify) {
        super(toUnify);
        this.statements = new ArrayList<>();
    }

    public StatementExecutorRule(List<ValueModel> toUnifyList, List<Statement> statements) {
        super(toUnifyList);
        this.statements = new ArrayList<>(statements);
    }

    public StatementExecutorRule(Predicate predicate, List<ValueModel> toUnifyList) {
        super(predicate, toUnifyList);
        statements = new ArrayList<>();
    }

    public StatementExecutorRule(Predicate predicate, List<ValueModel> toUnifyList, List<Statement> statements) {
        super(predicate, toUnifyList);
        this.statements = new ArrayList<>(statements);
    }

    public void addStatement(Statement statement){
        if(fixed) throw new IllegalStateException("Rule state is fixed. You can not change it anymore.");
        statements.add(statement);
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public boolean body(RuleContext context) {
        if(!fixed) throw new IllegalStateException("Rule state is not fixed. Call fix() before using it.");
        ExecutedStatements st = context.getStatements();

        while(st.currentStatement< this.statements.size() && st.currentStatement>st.cutIndex){
            PredicateContext predicateExecution;
            //if backtracked to executed statement, run same context
            if((st.currentStatement-st.cutIndex) == st.executions.size()){
                ExecutedStatement executedStatement = st.executions.get(st.executions.size() - 1);
                executedStatement.rollback();
                predicateExecution = executedStatement.getPredicateContext();
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
                //create new context from statement
                Managers managers = context.programContext().program().managers();
                predicateExecution = managers.getPredicateManager().context(statement.getPredicate(),
                        //copy statement args to getRule context
                        statement.getArgs().stream()
                                .map(value -> value.forContext(context))
                                .collect(Collectors.toList()),
                        context);
                //Save context and all variables backups
                st.executions.add(
                        new ExecutedStatement(
                                predicateExecution,
                                predicateExecution.getArgs().stream()
                                        .map(Value::innerFreeVariables)//Get lists of all variables in args (including ones in lists or functors)
                                        .reduce(new ArrayList<>(), //Concatenating lists of variables
                                                (v1, v2) -> {
                                            v1.addAll(v2);
                                            return v1;})
                                        .stream()//Making backups of all variables
                                        .map(v->managers.getBackupManager().backup(v))
                                        .collect(Collectors.toList())
                ));
            }
            if(predicateExecution.execute()){
                st.currentStatement++;
            }else{
                st.executions.remove(st.executions.size()-1);
                st.currentStatement--;
            }
        }
        return st.currentStatement > st.cutIndex;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        Collection<ModelStateException> exceptions = super.exceptions();
        for(Statement s : statements){
            exceptions.addAll(s.exceptions());
        }
        return exceptions;
    }

    @Override
    public ModelObject fix() {
        super.fix();
        statements = Collections.unmodifiableList(new ArrayList<>(statements));
        statements.forEach(Statement::fix);
        return this;
    }

    private boolean isCutPredicate(Predicate predicate){
        return predicate.getName().equals("!") || predicate.getName().equals("cut");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if(!statements.isEmpty()){
            sb.append(":-");
            sb.append(statements.get(0));
            for (int i=1;i<statements.size();i++){
                sb.append(", ").append(statements.get(i));
            }
        }
        return sb.toString();
    }
}
