package ru.prolog.runtime.database;

import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.rule.FactRule;
import ru.prolog.model.rule.Rule;
import ru.prolog.model.storage.database.DatabaseModel;
import ru.prolog.model.storage.database.exceptions.PredicateNotInDatabaseException;

import java.util.*;

/**
 * Реализация изменяемой базы данных.
 */
public class DatabaseImpl implements Database {
    /**
     * Модель базы данных, из которой создан этот объект.
     */
    private DatabaseModel model;

    /**
     * Отображение Имя_БД -> Предикат_БД -> Факты_предиката
     * <p>
     * По имени базы данных хранится {@link Map} правил для предикатов базы данных, находящихся в заданной БД.
     * В нём по ключу-предикату хранится список относящихся к предикату фактов.
     */
    private Map<String, Map<DatabasePredicate, List<FactRule>>> databases = new HashMap<>();

    /**
     * Создаёт объект изменяемой базы данных из моднли базы данных.
     *
     * @param model Модель базы данных, хранящая соответствие между именами баз данных и предикатами БД.
     */
    public DatabaseImpl(DatabaseModel model) {
        this.model = model;
        for (String dbName : model.databases()) {
            //Создание баз данных.
            Map<DatabasePredicate, List<FactRule>> db = new HashMap<>();
            databases.put(dbName, db);
            for (DatabasePredicate predicate : model.predicates(dbName)) {
                //Создание списка правил для каждого предиката.
                List<FactRule> rules = new ArrayList<>();
                db.put(predicate, rules);
                //Заполнение списка встроенными в предикат правилами.
                for (Rule rule : predicate.getRules()) {
                    rules.add((FactRule) rule);
                }
            }
        }
    }

    @Override
    public Collection<DatabasePredicate> databasePredicates() {
        List<DatabasePredicate> predicates = new ArrayList<>();
        for (Map<DatabasePredicate, List<FactRule>> db : databases.values()) {
            predicates.addAll(db.keySet());
        }
        return predicates;
    }

    @Override
    public Collection<DatabasePredicate> databasePredicates(String dbName) {
        List<DatabasePredicate> predicates = new ArrayList<>();
        for (Map<DatabasePredicate, List<FactRule>> db : databases.values()) {
            predicates.addAll(db.keySet());
        }
        return predicates;
    }

    @Override
    public Collection<String> databaseNames() {
        return databases.keySet();
    }

    @Override
    public String databaseName(String predicateName) {
        return model.databaseName(predicateName);
    }

    @Override
    public boolean contains(String predicateName) {
        return model.contains(predicateName);
    }

    @Override
    public boolean contains(String dbName, String predicateName) {
        return model.contains(dbName, predicateName);
    }

    @Override
    public DatabasePredicate get(String predicateName) {
        String dbName = databaseName(predicateName);
        if (dbName == null) return null;
        return get(dbName, predicateName);
    }

    @Override
    public DatabasePredicate get(String dbName, String predicateName) {
        Map<DatabasePredicate, List<FactRule>> db = databases.get(dbName);
        if (db == null) throw new IllegalArgumentException("Database with name '" + dbName + "' does not exist");
        return db.keySet().stream()
                .filter(p -> p.getName().equals(predicateName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<FactRule> getRules(DatabasePredicate predicate) {
        String dbName = databaseName(predicate.getName());
        if (dbName == null) throw new PredicateNotInDatabaseException(predicate.getName(), this);
        return databases.get(dbName).get(predicate);
    }

    @Override
    public void assertz(FactRule fact) {
        assert_(fact, false);
    }

    @Override
    public void asserta(FactRule fact) {
        assert_(fact, true);
    }

    /**
     * Общая логика для методов {@link #asserta(FactRule)} и {@link #assertz(FactRule)}.
     * Добавляет факт в начало или а конец списка правил в зависимости от {@param a}.
     *
     * @param fact Добавляемый в базу факт.
     * @param a    если {@code true}, вызывается {@link #asserta(FactRule)}, иначе {@link #assertz(FactRule)}.
     */
    private void assert_(FactRule fact, boolean a) {
        List<FactRule> rules = getRulesList(fact);
        if (a) rules.add(0, fact);
        else rules.add(fact);
    }

    @Override
    public void retract(FactRule fact) {
        List<FactRule> rules = getRulesList(fact);
        rules.remove(fact);
    }

    @Override
    public String save() {
        return save(DEFAULT_DB_NAME);
    }

    @Override
    public String save(String dbName) {
        Map<DatabasePredicate, List<FactRule>> db = databases.get(dbName);
        if (db == null) throw new IllegalArgumentException("Database \"" + dbName + "\" not exists");
        if (db.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (List<FactRule> ruleList : db.values()) {
            for (FactRule rule : ruleList) {
                sb.append(rule).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Возврщает список всех правил для предиката, соответствующего переданному правилу.
     * Проще говоря, возвращает список, в котором должно содержаться переданное правило
     * (вызывается в {@link #assert_(FactRule, boolean)}) для добавления правила в список и в {@link #retract(FactRule)} для удаления правила из списка.
     *
     * @param fact Факт, которому нужно найти соответствующий список правил.
     * @return Список правил, соответствующий предикату базы данных, которому принадлежит переданное правило.
     * @throws PredicateNotInDatabaseException если предикат, которому принадлежит правило, не содержится ни в одной базе данных.
     */
    private List<FactRule> getRulesList(FactRule fact) {
        fact.fix();//Just in case if it is not fixed and has exceptions
        DatabasePredicate predicate = (DatabasePredicate) fact.getPredicate();
        String dbName = databaseName(predicate.getName());
        if (dbName == null) throw new PredicateNotInDatabaseException(predicate.getName(), this);
        return databases.get(dbName).get(predicate);
    }
}