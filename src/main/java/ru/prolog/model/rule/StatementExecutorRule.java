package ru.prolog.model.rule;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.rule.statements.ExecutedStatements;
import ru.prolog.model.type.exceptions.WrongTypeException;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.context.rule.statements.ExecutedStatement;
import ru.prolog.model.type.Type;
import ru.prolog.service.Managers;
import ru.prolog.values.Value;
import ru.prolog.values.variables.Variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StatementExecutorRule extends AbstractRule {
    private List<Statement> statements;

    protected StatementExecutorRule(List<Value> toUnify) {
        super(toUnify);
    }

    @Override
    public boolean body(RuleContext context) {
        ExecutedStatements st = context.getStatements();

        while(st.currentStatement< this.statements.size() && st.currentStatement>st.cutIndex){
            PredicateContext predicateExecution;
            //if backtracked to executed statement, run same context
            if((st.currentStatement-st.cutIndex) == st.executions.size()){
                ExecutedStatement executedStatement = st.executions.get(st.executions.size() - 1);
                executedStatement.rollback();
                predicateExecution = executedStatement.predicateContext;
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
                Managers managers = context.programContext().program().getManagers();
                predicateExecution = managers.getPredicateManager().executable(statement.getPredicate(),
                        //copy statement args to rule context
                        statement.getArgs().stream()
                                .map(value -> value.forContext(context))
                                .collect(Collectors.toList()),
                        context);
                st.executions.add(
                        new ExecutedStatement(
                                predicateExecution,
                                //ToDo: ERROR! Arg can be list or functor containing variables which will not be backupped!
                                predicateExecution.getArgs().stream()
                                        .filter(v -> v instanceof Variable)
                                        .map(v->managers.getBackupManager().backup((Variable)v))
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

    private boolean isCutPredicate(Predicate predicate){
        return predicate.getName().equals("!") || predicate.getName().equals("cut");
    }

    public static class Builder implements Rule.RuleBuilder<StatementExecutorRule>{
        private final String name;
        private List<Value> toUnifyList;
        private StatementExecutorRule created;
        private List<Statement.StatementBuilder> statements;
        private boolean closed = false;

        public Builder(String name) {
            this.name = name;
        }

        @Override
        @SuppressWarnings("Duplicates")
        public void setPredicate(Predicate predicate) {
            if(created==null) throw new IllegalStateException("Rule not created");
            if(!predicate.getName().equals(name)) throw new IllegalArgumentException("Predicate functorName does not match rule functorName");
            if(predicate.getArgTypes().size()!=toUnifyList.size()) throw new IllegalArgumentException("Predicate args list has different size");
            for (int i = 0; i < predicate.getArgTypes().size(); i++) {
                Type predicateArgType = predicate.getTypeStorage().get(predicate.getArgTypes().get(i));
                Type ruleArgType = toUnifyList.get(i).getType();
                if(!predicateArgType.equals(ruleArgType)){
                    throw new WrongTypeException("Type of predicate and rule argument do not match.", predicateArgType, ruleArgType);
                }
            }
            created.predicate = predicate;
        }

        @Override
        public void addUnifyValue(Value val) {
            if(toUnifyList ==null) toUnifyList = new ArrayList<>();
            toUnifyList.add(val);
        }

        @Override
        public List<Value> getUnifyArgs() {
            return Collections.unmodifiableList(toUnifyList);
        }

        @Override
        public StatementExecutorRule close() {
            closed = true;
            return create();
        }

        @Override
        public StatementExecutorRule create() {
            if(created!=null) return created;
            if(toUnifyList==null) toUnifyList = Collections.emptyList();
            return created = new StatementExecutorRule(toUnifyList);
        }

        @Override
        public boolean isClosed() {
            return closed;
        }
    }
}
