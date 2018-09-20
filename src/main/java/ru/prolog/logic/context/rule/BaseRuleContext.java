package ru.prolog.logic.context.rule;


import ru.prolog.logic.backup.Backup;
import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.context.rule.statements.ExecutedStatements;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;

import java.util.*;

public class BaseRuleContext implements RuleContext {
    private Rule rule;
    private List<Value> args;
    private Map<String, Variable> variables;
    private ProgramContext programContext;
    private PredicateContext context;
    private List<Backup> backups;
    private ExecutedStatements statements;

    public BaseRuleContext(Rule rule, List<Value> args, ProgramContext programContext) {
        this.programContext = programContext;
        this.rule = rule;
        this.args = args;
    }

    public BaseRuleContext(Rule rule, List<Value> args, PredicateContext context) {
        this(rule, args, context.programContext());
        this.context = context;
    }

    public Rule getRule() {
        return rule;
    }

    public List<Value> getArgs() {
        return args;
    }

    @Override
    public Variable getVariable(String name) {
        if (variables == null) return null;
        return variables.get(name);
    }

    @Override
    public void addVariable(Variable variable) {
        if (variables == null) variables = new HashMap<>();
        variables.put(variable.getName(), variable);
    }

    @Override
    public Collection<Variable> getVariables() {
        if (variables == null) return Collections.emptyList();
        return variables.values();
    }

    @Override
    public ProgramContext programContext() {
        return programContext;
    }

    @Override
    public PredicateContext getPredicateContext() {
        return context;
    }

    @Override
    public boolean execute() {
        argBackups();
        if (rule.run(args, this)) {
            return true;
        } else {
            onFail();
            return false;
        }
    }

    private void argBackups() {
        backups = new LinkedList<>();
        for (Value arg : args) {
            for (Variable var : arg.innerFreeVariables()) {
                backups.add(programContext.program().managers().getBackupManager().backup(var));
            }
        }
    }

    public void rollback() {
        backups.forEach(Backup::rollback);
    }

    @Override
    public boolean redo() {
        if (statements == null || statements.executions.isEmpty()) {
            onFail();
            return false;
        }
        getStatements().currentStatement--;
        getStatements().executions.get(getStatements().executions.size() - 1).rollback();
        if (rule.run(args, this)) {
            return true;
        } else {
            onFail();
            return false;
        }
    }

    @Override
    public ExecutedStatements getStatements() {
        if (statements == null) statements = new ExecutedStatements();
        return statements;
    }

    private void onFail() {
        rollback();
        deleteVariables();
    }

    /**
     * Все переменные в удаляемом контексте удаляются из дерева связей переменных.
     * Для каждой переменной берётся первая из связанных, и к ней присоединяются все остальные связанные.
     */
    private void deleteVariables() {
        if (variables == null) return;
        //Цикл по всем переменным в контексте
        for (Variable var : variables.values()) {
            //Если у переменной нет связей, она пропускается
            if (var.getRelated().isEmpty()) continue;
            //Список связей копируется для безопасного удаления связей в цикле без риска получить ConcurrentModificationException
            List<Variable> related = new ArrayList<>(var.getRelated());
            //Выделяется первая из связанный переменых, и связь между ними удаляется
            Variable first = related.get(0);
            var.removeRelated(first);
            //Первая связанная переменная удаляется из списка чтобы не участвовала в цикле
            related.remove(0);
            //Каждая из оставшихся связяных переменных отвязывается от удаляемой и привязывается к первой.
            for (Variable rel : related) {
                var.removeRelated(rel);
                first.addRelated(rel);
            }
            //Теперь переменная больше не участвует в дереве связей, и после очистки variables может удалиться сборщиком мусора
        }
        variables.clear();
    }
}