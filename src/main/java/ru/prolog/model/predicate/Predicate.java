package ru.prolog.model.predicate;

import ru.prolog.model.ModelObject;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.type.Type;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.util.keys.PredicateKeys;

import java.util.List;

/**
 * Базовый интерфейс для предикатов.
 * <b>Не рекомендуется реализовывать этот интерфейс!</b> Для создания собственных предикатов лучше унаследовать класс от {@link AbstractPredicate}!
 */
public interface Predicate extends ModelObject {
    /**
     * Ключ, по которому в контексте предиката предикаты могут хранить номер правила
     */
    String KEY_START_RULE = PredicateKeys.START_WITH_RULE;

    /**
     * Возвращает имя предиката
     *
     * @return Имя предиката. Не должно изменяться между вызовами!
     */
    String getName();

    /**
     * Возвращает имена типов аргументов предиката.
     * Типы по этим именам можно найти в {@link #getTypeStorage()}
     * @return Имена типов аргументов предиката. Пустой список, если предикат не принимает аргументов.
     */
    List<String> getArgTypeNames();

    /**
     * Возвращает список типов аргументов.
     * Типы можно получить по {@link #getArgTypeNames()}, используя {@link #getTypeStorage()}
     * @return Список типов аргументов. Пустой список, если предикат не принимает аргументов.
     */
    List<Type> getArgTypes();

    /**
     * Возвращает количество аргументов, принимаемых предикатом.
     * @return Кколичество аргументов в предикате. Если последний аргумент vararg, возвращает Integer.MAX_VALUE
     */
    int getArity();

    /**
     * Возвращает хранилище типов, используемое для преобразования имён типов из {@link #getArgTypeNames()} в типы в {@link #getArgTypes()}
     * Может также использоваться в методе {@link #run(PredicateContext, List)}
     * @return Хранилище типов. Может быть {@code null}, если предикат не принимает аргументов. Рекомендуется принимать {@link TypeStorage} в конструкторе предиката.
     */
    TypeStorage getTypeStorage();

    /**
     * Основной метод, определяющий логику предиката.
     * @param args аргументы, переданные при вызове предиката
     * @return Итог выполнения: решение не найдено {@link PredicateResult#FAIL}, найдено очередное решение ({@link PredicateResult#NEXT_RESULT}), найдено последнее решение ({@link PredicateResult#LAST_RESULT})
     */
    PredicateResult run(PredicateContext context, List<Value> args);
}
