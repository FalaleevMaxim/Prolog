package ru.prolog.runtime.context.rule;


import ru.prolog.etc.backup.Backup;
import ru.prolog.model.rule.Rule;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.statements.ExecutedStatement;
import ru.prolog.runtime.context.rule.statements.ExecutedStatements;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.util.ToStringUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Базовая реализация {@link RuleContext}, реализующая все основные функции, описанные в интерфейсе.
 */
public class BaseRuleContext implements RuleContext {
    /**
     * Вызываемое в контексте правило.
     */
    private Rule rule;

    /**
     * Аргументы, передаваемые правилу при вызове.
     */
    private List<Value> args;

    /**
     * Отображает имя переменной на переменную. Инициализируется при регистрации первой переменной в контексте.
     */
    private Map<String, Variable> variables;

    /**
     * Ссылка на контекст программы, в рамках которого выполняется правило.
     */
    private ProgramContext programContext;

    /**
     * Контекст предиката, из которого было вызвано правило.
     */
    private PredicateContext context;

    /**
     * Бэкапы переменных в аргументах правила на момент перед первым вызовом правила.
     * Инициализируется при вызове правила.
     */
    private List<Backup> backups;

    /**
     * Объект, хранящий информацию о выполненных выражениях.
     */
    private ExecutedStatements statements;

    /**
     * @param rule    Вызываемое правило.
     * @param args    Аргументы, передаваемые правилу при вызове.
     * @param context Контекст предиката, из которого вызывается правило.
     */
    public BaseRuleContext(Rule rule, List<Value> args, PredicateContext context) {
        this.programContext = context.programContext();
        this.context = context;
        this.rule = rule;
        this.args = args;
    }

    public Rule rule() {
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

    /**
     * Создаёт бэкапы для всех переменных в аргументах, передаваемых правилу при вызове.
     */
    private void argBackups() {
        backups = args.stream()
                .map(Value::innerFreeVariables)
                .flatMap(Collection::stream)
                .map(programContext.program().managers().getBackupManager()::backup)
                .collect(Collectors.toList());
    }

    public void rollback() {
        if (backups == null) return;
        backups.forEach(Backup::rollback);
    }

    @Override
    public boolean redo() {
        if (statements == null || statements.executions.isEmpty()) {
            onFail();
            return false;
        }

        getStatements().currentStatement--;
        ExecutedStatement executedStatement = getStatements().executions.get(getStatements().executions.size() - 1);
        if (executedStatement != null) {
            executedStatement.rollback();
        }

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
    //ToDo не уверен, что этот метод нужен, т.к. при fail правила переменные откатываются к моменту до вызова.
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

    @Override
    public String toString() {
        return "Rule " + rule + " called as " + ToStringUtil.funcToString(rule.getPredicate().getName(), args);
    }
}