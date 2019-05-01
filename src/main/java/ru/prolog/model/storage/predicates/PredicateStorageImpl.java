package ru.prolog.model.storage.predicates;

import org.reflections.Reflections;
import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.AbstractModelObject;
import ru.prolog.model.predicate.*;
import ru.prolog.model.rule.Rule;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.model.storage.predicates.exceptions.SamePredicateException;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.type.Type;
import ru.prolog.model.values.ValueModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class PredicateStorageImpl extends AbstractModelObject implements PredicateStorage {
    private Map<String, SortedMap<Integer, Predicate>> predicates = new HashMap<>();
    private TypeStorage typeStorage;

    public PredicateStorageImpl(TypeStorage typeStorage) {
        this.typeStorage = typeStorage;
        addBuiltInPredicates();
    }

    @Override
    public Collection<Predicate> all() {
        List<Predicate> all = new ArrayList<>();
        predicates.values().forEach(pr -> all.addAll(pr.values()));
        return all;
    }

    @Override
    public Collection<Predicate> get(String name) {
        name = name.toLowerCase();
        if (!predicates.containsKey(name)) return Collections.emptyList();
        return predicates.get(name).values();
    }

    @Override
    public Predicate get(String name, int arity) {
        name = name.toLowerCase();
        if (!predicates.containsKey(name)) return null;
        return predicates.get(name).get(arity);
    }

    @Override
    public Predicate getVarArgPredicate(String name) {
        return get(name, Integer.MAX_VALUE);
    }

    @Override
    public Predicate getFitting(String name, List<Type> types) {
        name = name.toLowerCase();
        if (!predicates.containsKey(name)) return null;
        Predicate p = get(name, types.size());
        if (p == null) p = getVarArgPredicate(name);
        if (p != null) {
            if (matchArgTypes(p, types) == types.size())
                return p;
            else p = null;
        }
        int match = -1;
        for (Map.Entry<Integer, Predicate> entry : predicates.get(name).entrySet()) {
            int m = matchArgTypes(entry.getValue(), types);
            if (m > match) {
                match = m;
                p = entry.getValue();
            }
        }
        return p;
    }

    @Override
    public void add(Predicate predicate) {
        if (fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        SortedMap<Integer, Predicate> predMap;
        String name = predicate.getName().toLowerCase();
        if (!predicates.containsKey(name)) {
            predMap = new TreeMap<>();
            predicates.put(name, predMap);
        } else {
            predMap = predicates.get(name);
        }
        int arity = predicate.getArity();
        if (!predMap.containsKey(arity)) {
            predMap.put(arity, predicate);
        } else {
            if (!isBuiltInPredicate(predicate))
                throw new SamePredicateException(predicate, predMap.get(arity));
        }
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if (fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();
        for (Predicate p : all()) {
            //This horrible construction sets predicates to statements in rules if they are not set.
            if (p instanceof RuleExecutorPredicate) {
                for (Rule r : ((RuleExecutorPredicate) p).getRules()) {
                    if (r instanceof StatementExecutorRule) {
                        for (List<Statement> list : ((StatementExecutorRule) r).getStatements()) {
                            for (Statement st : list) {
                                if (st.getPredicate() == null) {
                                    Predicate predicate = getFitting(
                                            st.getPredicateName(),
                                            st.getArgs().stream()
                                                    .map(ValueModel::getType)
                                                    .collect(Collectors.toList()));
                                    if (predicate != null) st.setPredicate(predicate);
                                }
                            }
                        }
                    }
                }
            }
            exceptions.addAll(p.exceptions());
        }
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        all().forEach(Predicate::fix);
    }

    @Override
    public String toString() {
        StringBuilder prSb = new StringBuilder("predicates\n");
        StringBuilder clSb = new StringBuilder("clauses\n");
        for (Predicate p : all()) {
            if (p instanceof PrologPredicate) {
                ((PrologPredicate) p).getRules().forEach(
                        rule -> clSb.append("\t").append(rule).append(".\n"));
                if (!(p instanceof DatabasePredicate)) {
                    prSb.append("\t").append(p).append("\n");
                }
            }
        }
        return prSb.append(clSb).toString();
    }

    /**
     * Сканирует пакет  {@link ru.prolog.std}, ищет все неабстрактные классы предикатов, создаёт и {@link #add(Predicate) добавляет} в хранилище объекты.
     * В ранних версиях все стандартные предикаты приходилось создавать и добавлять вручную в этом методе.
     */
    private void addBuiltInPredicates() {
        Reflections reflections = new Reflections("ru.prolog.std");
        reflections.getSubTypesOf(AbstractPredicate.class).stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .map(this::instantiate)
                .forEach(this::add);
    }

    /**
     * Проверяет, является ли предикат встроенным.
     * Все встроенные предикаты находятся в пакете {@link ru.prolog.std}
     *
     * @param p Проверяемый предикат.
     * @return true если класс предиката находится внутри {@link ru.prolog.std}
     */
    @Override
    public boolean isBuiltInPredicate(Predicate p) {
        return p.getClass().getPackage().getName().startsWith("ru.prolog.std");
    }

    /**
     * Служебный метод для метода {@link #addBuiltInPredicates()}, создающий объекты предикатов.
     * Получает класс предиката, находит конструктор без параметров или конструктор, принимающий {@link TypeStorage} и создаёт объект с помощью конструктора
     * @throws IllegalStateException Если не удалось создать объект.
     * @param pClass класс предиката
     * @return объект переданного класса
     */
    private Predicate instantiate(Class<? extends AbstractPredicate> pClass) {
        for (Constructor<?> constructor : pClass.getConstructors()) {
            if (constructor.getParameterCount() > 1) continue;
            if (constructor.getParameterCount() == 0) {
                try {
                    return (Predicate) constructor.newInstance();
                } catch (Exception ignored) {
                    continue;
                }
            }
            if (constructor.getParameters()[0].getType().equals(TypeStorage.class)) {
                try {
                    return (Predicate) constructor.newInstance(typeStorage);
                }catch (Exception ignored){
                }
            }
        }
        throw new IllegalStateException("Error instantiating predicate class" + pClass.getName());
    }
}
