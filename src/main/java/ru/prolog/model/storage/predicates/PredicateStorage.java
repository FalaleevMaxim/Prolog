package ru.prolog.model.storage.predicates;

import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.type.Type;

import java.util.Collection;
import java.util.List;

/**
 * Хранилище предикатов.
 * Хранит предикаты по имени и арности.
 * Имя не чувствительно к регистру. Таким образом, предикат с именем "makeWindow" можно найти и по строке "makewindow".
 */
public interface PredicateStorage extends ModelObject {
    /**
     * Возвращает коллекцию всех предикатов в хранилище
     *
     * @return Все предикаты в хранилище.
     */
    Collection<Predicate> all();

    /**
     * Возвращает все предикаты с заданным именем.
     * Имя не зависит от регистра
     * @param name Имя предиката
     * @return Коллекция всех предикатов с заданным именем. Если таких нет, то пустой список.
     */
    Collection<Predicate> get(String name);

    /**
     * Возвращает предикат с заданным именем и арностью.
     * Имя не зависит от регистра
     * @param name Имя предиката
     * @param arity арность предиката (количество аргументов). Для vararg предикатов арнсть равна {@link Integer#MAX_VALUE}.
     * @return Предикаты с заданным именем и арностью или {@code null} если такого нет.
     */
    Predicate get(String name, int arity);

    /**
     * Возвращает vararg предикат с заданным именем. Аналогичен вызову {@code get(name, Integer.MAX_VALUE);}
     * Имя не зависит от регистра.
     * @param name Имя предиката
     * @return Предикаты с заданным именем и арностью или {@code null} если такого нет.
     */
    Predicate getVarArgPredicate(String name);

    /**
     * Ищет предикат с заданным именем, наиболее подходящий по списку типов.
     * В первую очередь находит полное совпадение по списку типов.
     * Если по списку типов совпадения нет, проверяет vararg предикат.
     * Если нет совпадений и для vararg, ищет предикат, у которого совпадает больше всего типов аргументов.
     * Таким образом, найденный предикат может не подходить к набору аргументов!
     * @param name Имя предиката.
     * @param types Типы аргументов
     * @return {@code null} Если нет предикатов с таким именем. Среди предикатов с данным именем возвращает наиболее подходящий по списку аргументов
     */
    Predicate getFitting(String name, List<Type> types);

    /**
     * Добавляет предикат в хранилище.
     * @param predicate Добавляемый предикат.
     */
    void add(Predicate predicate);

    /**
     * Проверяет, является ли предикат встроенным.
     * @param p Проверяемый предикат.
     * @return true для встроенных предикатов.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isBuiltInPredicate(Predicate p);

    /**
     * Подсчитывает, сколько типов аргументов совпадают с типами аргументов предиката.
     * Работает и для vararg предикатов, успешно сопоставляя произвольное число аргументов с vararg-типом.
     *
     * @param p     Предиткат
     * @param types Типы аргументов
     * @return Количество переданных аргументов, совпадающих с типами аргументов предиката.
     */
    default int matchArgTypes(Predicate p, List<Type> types) {
        int count = 0;
        boolean varArg = false;
        for (int i = 0; (i < p.getArgTypeNames().size() || varArg) && i < types.size(); i++) {
            Type predType;
            if (varArg) {
                predType = p.getTypeStorage().get(
                        p.getArgTypeNames().get(p.getArgTypeNames().size() - 1));
            } else {
                predType = p.getTypeStorage().get(p.getArgTypeNames().get(i));
                if (predType.isVarArg()) varArg = true;
            }
            Type reqType = types.get(i);
            if (predType.isCommonType() || predType.equals(reqType)) {
                count++;
            }
        }
        return count;
    }
}