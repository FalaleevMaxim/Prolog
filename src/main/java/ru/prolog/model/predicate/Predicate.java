package ru.prolog.model.predicate;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.ModelObject;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.Value;

import java.util.List;

public interface Predicate extends ModelObject {
    String getName();

    /**
     * @return names of predicate arguments type. Types by these names can be found in {@link #getTypeStorage()}
     */
    List<String> getArgTypes();

    /**
     * @return Type storage to search types from {@link #getArgTypes()} method. Can be null if predicate has no args. It is advised to take TypeStorage in your predicate constructor.
     */
    TypeStorage getTypeStorage();

    /**
     * What predicate should do.
     * @param args arguments sent ro predicate
     * @param startWith number of rule to start with. It is 0 on first execution and last return +1 on next launches.
     * @return number of rule which returned true. -1 if no rule returned true or predicate returns fail. On next execution {@param startWith} will be returned value +1.
     */
    int run(PredicateContext context, List<Value> args, int startWith);
}
