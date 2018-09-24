package ru.prolog.logic.model.predicate;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.storage.type.TypeStorage;

import java.util.List;

/**
 * Базовый интерфейс для предикатов.
 * <b>Не рекомендуется реализовывать этот интерфейс!</b> Для создания собственных предикатов лучше унаследовать класс от {@link AbstractPredicate}!
 */
public interface Predicate extends ModelObject {
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
     * Может также использоваться в методе {@link #run(PredicateContext, List, int)}
     * @return Хранилище типов. Может быть {@code null}, если предикат не принимает аргументов. Рекомендуется принимать {@link TypeStorage} в конструкторе предиката.
     */
    TypeStorage getTypeStorage();

    /**
     * Основной метод, определяющий логику предиката.
     * @param args аргументы, переданные при вызове предиката
     * @param startWith Номер правила (или варианта выполнения), с которого нужно начать поиск решения.
     *                  При первом вызове равно 0.
     *                  При каждом последующем вызове при бэктрекинге будет на 1 больше последнего возвращённого в методе числа.
     * @return Номер правила (или варианта выполнения), завершившегося успешно. -1 если предикат возвращает fail. При следующем вызове {@param startWith} будет на 1 больше возвращённого числа.
     */
    int run(PredicateContext context, List<Value> args, int startWith);
}
