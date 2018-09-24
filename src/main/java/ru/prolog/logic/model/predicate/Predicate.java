package ru.prolog.logic.model.predicate;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.storage.type.TypeStorage;

import java.util.List;

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
