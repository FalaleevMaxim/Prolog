package ru.prolog.model.rule;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.etc.exceptions.model.rule.RuleStateException;
import ru.prolog.model.managers.Managers;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.values.ValueModel;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.context.rule.statements.ExecutedStatement;
import ru.prolog.runtime.context.rule.statements.ExecutedStatements;
import ru.prolog.runtime.values.Value;

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
            while (st.currentStatement < statList.size() && st.currentStatement > st.cutIndex) statements:{
                PredicateContext predicateExecution;
                //При бектрекинге нужно запустить тот же контекст
                if ((st.currentStatement - st.cutIndex) == st.executions.size()) {
                    ExecutedStatement executedStatement = st.executions.get(st.executions.size() - 1);
                    //Выражения, которые не произведут новых результатов, становятся null, и их нужно пропустить при возврате.
                    while (executedStatement == null) {
                        st.currentStatement--;
                        st.executions.remove(st.currentStatement - st.cutIndex);
                        if (st.currentStatement <= st.cutIndex) {
                            break statements;
                        }
                        executedStatement = st.executions.get(st.executions.size() - 1);
                    }
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
                    //Создание нового контекста вызова предиката с помощью менеджера предикатов
                    Managers managers = context.programContext().program().managers();
                    predicateExecution = managers.getPredicateManager().context(statement,
                            //copy statement args to getRule context
                            statement.getArgs().stream()
                                    .map(value -> value.forContext(context))
                                    .collect(Collectors.toList()),
                            context);
                    //Сохранение контекста и бэкапов переменных в аргументах предиката.
                    st.executions.add(
                            new ExecutedStatement(
                                    predicateExecution,
                                    predicateExecution.getArgs().stream()
                                            .map(Value::innerFreeVariables)//Получение списков всех свободных переменных в аргументах (включая переменные внутри списков и функторов)
                                            .flatMap(List::stream)//Объединение списков
                                            .map(v -> managers.getBackupManager().backup(v))//Создание бэкапов
                                            .collect(Collectors.toList())
                            ));
                }
                PredicateResult predicateResult = predicateExecution.execute();
                if (predicateResult.toBoolean()) {
                    //Если предикат не произведёт новых решений, вместо него в список выполненных ывражений ставится null.
                    //Удалить его из списка нельзя, т.к. номер в списке выполненных выражений соответствует номеру выражения (с учётом индекса отсечения)
                    //Удаление производится, поскольку иначе выражение вернёт fail - поэтому нет смысла хранить и вызывать лишний объект.
                    if (predicateResult == PredicateResult.LAST_RESULT) {
                        //st.currentStatement - то номер выражения в теле правила. Номер выражения в списке выполненных вычисляется с учётом индекса отсечения
                        st.executions.set(st.currentStatement - (st.cutIndex + 1), null);
                    }
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
