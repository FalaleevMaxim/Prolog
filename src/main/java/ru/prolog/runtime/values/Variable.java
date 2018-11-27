package ru.prolog.runtime.values;

import ru.prolog.etc.backup.Backup;
import ru.prolog.runtime.context.rule.RuleContext;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Интерфейс для всех переменных в программе.
 * <p>
 * Этот интерфейс представляет значения во время выполнения программы.
 * В аргументах правил и выражений в модели программы используется {@link ru.prolog.model.values.VariableModel}.
 */
public interface Variable extends Value {
    /**
     * Возвращает контекст вызова правила, в котором была создана (и скорее всего зарегистрирована) переменная.
     *
     * @return Контекст вызова правила, в котором была создана переменная.
     */
    RuleContext getRuleContext();

    /**
     * Возвращает имя переменной.
     *
     * @return Имя переменной.
     */
    String getName();

    /**
     * Проверяет, является ли переменная свободной.
     *
     * @return {@code true} если переменная свободная, иначе {@code false}.
     */
    boolean isFree();

    /**
     * Возвращает множество переменных, связанных с данной напрямую.
     * <p>
     * После унификации двух свободный переменных эти переменные добавляют друг друга в связанные,
     * и конда переменная получает содержимое, она передаёт его всем связанным переменным.
     * <p>
     * Переменные не добавляют друг друга в связанные, если они уже связаны напрямую ({@link #isRelated(Variable)}) или транзитивно ({@link #isImplicitlyRelated(Variable)})!
     *
     * @return
     */
    Set<? extends Variable> getRelated();

    boolean isRelated(Variable variable);

    default boolean isImplicitlyRelated(Variable variable) {
        Set<Variable> seen = new HashSet<>();
        Deque<Variable> traverse = new LinkedList<>();
        traverse.push(this);
        while (!traverse.isEmpty()) {
            Variable var = traverse.pop();
            if (var == variable) return true;
            if (!seen.add(var)) continue;
            traverse.addAll(var.getRelated());
        }
        return false;
    }

    Backup getLastBackup();

    void setLastBackup(Backup backup);

    /**
     * Переменные не добавляют друг друга в связанные, если они уже связаны напрямую ({@link #isRelated(Variable)}) или транзитивно ({@link #isImplicitlyRelated(Variable)})!
     * Таким образом, связи между переменными представляют собой дерево, и не содержат циклов.
     *
     * @param variable Переменная, которую нужно добавить в связанные.
     */
    void addRelated(Variable variable);

    /**
     * Удаляет переменную из множества связанных.
     * Используется при бэктрекинге для удаления связей при откате состояния переменной.
     *
     * @param variable Переменная, связь с которой нужно удалить.
     */
    void removeRelated(Variable variable);

    /**
     * Очищает содержимое переменной.
     * Используется <b>только</b> при бэктрекинге!
     * <b>Не использовать в предикатах или где-либо при проходе программы вперёд!</b>
     */
    void setFree();
}