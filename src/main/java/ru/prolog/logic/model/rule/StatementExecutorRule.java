package ru.prolog.logic.model.rule;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.rule.statements.ExecutedStatements;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.exceptions.rule.RuleStateException;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.context.rule.statements.ExecutedStatement;
import ru.prolog.logic.managers.Managers;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.model.values.ValueModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StatementExecutorRule extends AbstractRule {
    private List<List<Statement>> statements;

    public StatementExecutorRule() {
        statements = new ArrayList<>();
        statements.add(new ArrayList<>());
    }

    public StatementExecutorRule(Predicate predicate){
        setPredicate(predicate);
        this.statements = new ArrayList<>();
        statements.add(new ArrayList<>());
    }

    public StatementExecutorRule(List<ValueModel> toUnify) {
        super(toUnify);
        this.statements = new ArrayList<>();
        statements.add(new ArrayList<>());
    }

    public StatementExecutorRule(List<ValueModel> toUnifyList, List<Statement> statements) {
        super(toUnifyList);
        this.statements = new ArrayList<>();
        this.statements.add(new ArrayList<>(statements));
    }



    public StatementExecutorRule(Predicate predicate, List<ValueModel> toUnifyList) {
        super(predicate, toUnifyList);
        statements = new ArrayList<>();
        statements.add(new ArrayList<>());
    }

    public StatementExecutorRule(Predicate predicate, List<ValueModel> toUnifyList, List<Statement> statements) {
        super(predicate, toUnifyList);
        this.statements = new ArrayList<>();
        this.statements.add(new ArrayList<>(statements));
    }

    public void addStatement(Statement statement){
        if(fixed) throw new IllegalStateException("Rule state is fixed. You can not change it anymore.");
        statements.get(statements.size()-1).add(statement);
    }

    public void addStatement(Statement statement, int listNum){
        if(fixed) throw new IllegalStateException("Rule state is fixed. You can not change it anymore.");
        statements.get(listNum).add(statement);
    }

    /**
     * Creates new list of statements.
     */
    public void or(){
        if(fixed) throw new IllegalStateException("Rule state is fixed. You can not change it anymore.");
        statements.add(new ArrayList<>());
    }

    public List<List<Statement>> getStatements() {
        return statements;
    }

    public List<Statement> getStatements(int listNum) {
        return statements.get(listNum);
    }

    public boolean hasStatements(){
        return !statements.get(0).isEmpty();
    }

    @Override
    public boolean body(RuleContext context) {
        if(!fixed) throw new IllegalStateException("Rule state is not fixed. Call fix() before using it.");
        ExecutedStatements st = context.getStatements();

        while (st.currentList<this.statements.size()) {
            List<Statement> statList = getStatements(st.currentList);
            while (st.currentStatement < statList.size() && st.currentStatement > st.cutIndex) {
                PredicateContext predicateExecution;
                //if backtracked to executed statement, run same context
                if ((st.currentStatement - st.cutIndex) == st.executions.size()) {
                    ExecutedStatement executedStatement = st.executions.get(st.executions.size() - 1);
                    executedStatement.rollback();
                    predicateExecution = executedStatement.getPredicateContext();
                } else {
                    Statement statement = statList.get(st.currentStatement);
                    //if statement is cut, remove all executions and save cut index
                    if (isCutPredicate(statement.getPredicate())) {
                        st.executions.clear();
                        st.cutIndex = st.currentStatement;
                        if (context.getPredicateContext() != null)
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
                                            .map(Value::innerFreeVariables)//Get lists of all free variables in args (including ones in lists or functors)
                                            .reduce(new ArrayList<>(), //Concatenating lists of variables
                                                    (v1, v2) -> {
                                                        v1.addAll(v2);
                                                        return v1;
                                                    })
                                            .stream()//Making backups of all variables
                                            .map(v -> managers.getBackupManager().backup(v))
                                            .collect(Collectors.toList())
                            ));
                }
                if (predicateExecution.execute()) {
                    st.currentStatement++;
                } else {
                    st.executions.remove(st.executions.size() - 1);
                    st.currentStatement--;
                }
            }
            if(st.currentStatement > st.cutIndex) return true;
            if(st.cutIndex>=0) return false;
            if(!st.executions.isEmpty()) st.executions.get(0).rollback();
            st.executions.clear();
            st.currentStatement=0;
            st.currentList++;
        }
        return false;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        Collection<ModelStateException> exceptions = super.exceptions();
        for (List<Statement> list : statements) {
            if(statements.size()>1 && list.isEmpty())
                exceptions.add(new RuleStateException(this, "One of rule lists in rule " + this + " is empty."));
            for(Statement s : list){
                exceptions.addAll(s.exceptions());
            }
        }

        return exceptions;
    }

    @Override
    public void fixIfOk() {
        super.fixIfOk();
        for (int i = 0; i < statements.size(); i++) {
            statements.get(i).forEach(Statement::fix);
            statements.set(i, Collections.unmodifiableList(statements.get(i)));
        }
        statements = Collections.unmodifiableList(new ArrayList<>(statements));
    }

    private boolean isCutPredicate(Predicate predicate){
        return predicate.getName().equals("!") || predicate.getName().equals("cut");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if(!statements.get(0).isEmpty()){
            sb.append(":- ");
            for (int i = 0; i < statements.size(); i++) {
                if(i!=0) sb.append("; ");
                for (int j=0;j<statements.get(i).size();j++){
                    if(j!=0) sb.append(", ");
                    sb.append(statements.get(i).get(j));
                }
            }
        }
        return sb.toString();
    }
}
