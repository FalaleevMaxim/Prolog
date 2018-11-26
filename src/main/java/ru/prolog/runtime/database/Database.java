package ru.prolog.runtime.database;

import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.rule.FactRule;
import ru.prolog.model.storage.database.DatabaseModel;
import ru.prolog.model.storage.database.exceptions.PredicateNotInDatabaseException;

import java.util.Collection;
import java.util.List;

/**
 * Изменяемая в процессе выполнения программы база данных, содержащая факты для предикатов базы данных.
 * Один объект содержит все базы данных, объявленные в программе.
 * <p>
 * Предикаты БД получают факты только из этой базы и не пользуются правилами, записанными в самом предикате {@link DatabasePredicate}.
 * Факты, записанные в предикате базы данных, добавляются в базу данных.
 */
public interface Database {
    /**
     * Имя базы данных по умолчанию.
     */
    String DEFAULT_DB_NAME = DatabaseModel.DEFAULT_DB_NAME;

    /**
     * Возвращает все предикаты базы данных
     *
     * @return Все предикаты базы данных
     */
    Collection<DatabasePredicate> databasePredicates();

    /**
     * Возвращает все предикаты, относящиеся к базе данных с указанным именем.
     *
     * @param dbName имя базы данных
     * @return Все предикаты, относящиеся к указанной базе данных. Пустая коллекция, если базы данных с таким именем не существует.
     */
    Collection<DatabasePredicate> databasePredicates(String dbName);

    /**
     * Возвращает имена всех баз данных.
     *
     * @return Имена всех баз данных.
     */
    Collection<String> databaseNames();

    /**
     * Возвращает имя базы данных, к которой принадлежит предикат с заданным именем
     *
     * @param predicateName Имя предиката базы данных.
     * @return Имя базы данных, содержащей предикат с заданным именем. {@code null}, если ни одна база данных не содержит предиката с таким именем.
     */
    String databaseName(String predicateName);

    /**
     * Проверяет, содержится ли предикат с заданным именем в базе данных.
     *
     * @param predicateName имя предиката.
     * @return {@code true} если какая-либо база данных содержит предикат с заданным именем; иначе {@code false}
     */
    boolean contains(String predicateName);

    /**
     * Проверяет, содержит ли база данных с именем {@param dbName} предикат с именем {@param predicateName}.
     *
     * @param dbName        Имя базы данных, в которой нужно искать предикатю
     * @param predicateName Имя предиката, который нужно найти в базе.
     * @return {@code true} Если предикат найден в указанной базе данных. {@code false} если указанная база данных не содержит такого предиката.
     * @throws IllegalArgumentException Если базы данных с указанным именем не существует.
     */
    boolean contains(String dbName, String predicateName);

    /**
     * Возвращает предикат базы данных по его имени.
     *
     * @param predicateName имя предиката.
     * @return Предикат с заданным именем или {@code null}, если предиката с таким именем нет в базе данных.
     */
    DatabasePredicate get(String predicateName);

    /**
     * Возвращает предикат базы данных, находящийся в указанной базе данных, по его имени.
     *
     * @param dbName        Имя базы данных.
     * @param predicateName Имя предиката.
     * @return Предикат в заданной базе данных с указанным именем. {@code null}, если предикат с таким именем не содержится в указанной базе данных.
     * @throws IllegalArgumentException Если базы данных с указанным именем не существует.
     */
    DatabasePredicate get(String dbName, String predicateName);

    /**
     * Возвращает список фактов, относящийся к данному предикату
     *
     * @param predicate предикат базы данных.
     * @return Список правил, относящихся к данному предикату.
     * @throws PredicateNotInDatabaseException если предикат не содержится ни в одной базе данных.
     */
    List<FactRule> getRules(DatabasePredicate predicate);

    /**
     * Добавляет факт в конец списка правил для соответствующего предиката базы данных.
     *
     * @param rule Добавляемое правило.
     * @throws PredicateNotInDatabaseException если предикат, которому принадлежит правило, не содержится ни в одной базе данных.
     */
    void assertz(FactRule rule);

    /**
     * Добавляет факт в начало списка правил для соответствующего предиката базы данных.
     *
     * @param rule Добавляемое правило.
     * @throws PredicateNotInDatabaseException если предикат, которому принадлежит правило, не содержится ни в одной базе данных.
     */
    void asserta(FactRule rule);

    /**
     * Удаляет первое вхождение факта в список правил соответствующего предиката.
     *
     * @param fact Удаляемое правило базы данных.
     */
    void retract(FactRule fact);

    /**
     * Возвращает факты, записанные в базе данных по умолчанию, в виде текста.
     * Каждый факт записывается с новой строки.
     *
     * @return Текст, содержащий факты базы данных по умолчанию, по одному правилу на строке.
     */
    String save();

    /**
     * Возвращает факты, записанные в указанной базе данных, в виде текста.
     * Каждый факт записывается с новой строки.
     *
     * @param dbName Имя базы данных.
     * @return Текст, содержащий факты указанной базы данных, по одному правилу на строке.
     * @throws IllegalArgumentException Если базы данных с указанным именем не существует.
     */
    String save(String dbName);
}