package ru.prolog.model.predicate;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.ModelObject;
import ru.prolog.model.type.Type;
import ru.prolog.model.type.descriptions.CommonType;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.Value;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface Predicate extends ModelObject {
    String getName();

    /**
     * @return names of predicate arguments type. Types by these names can be found in {@link #getTypeStorage()}
     */
    List<String> getArgTypeNames();

    /**
     * @return list of argument types. Gets types from {@link #getArgTypeNames()} using {@link #getTypeStorage()}
     */
    List<Type> getArgTypes();

    /**
     *
     * @return Count of arguments predicate accepts. If last argument type is vararg, returns Integer.MAX_VALUE
     */
    int getArity();

    /**
     * @return Type storage to search types from {@link #getArgTypeNames()} method. Can be null if predicate has no args. It is advised to take TypeStorage in your predicate constructor.
     */
    TypeStorage getTypeStorage();

    /**
     * What predicate should do.
     * @param args arguments sent ro predicate
     * @param startWith number of getRule to start with. It is 0 on first execution and last return +1 on next launches.
     * @return number of getRule which returned true. -1 if no getRule returned true or predicate returns fail. On next execution {@param startWith} will be returned value +1.
     */
    int run(PredicateContext context, List<Value> args, int startWith);
}
