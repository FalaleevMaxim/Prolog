package ru.prolog.std;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.runtime.values.simple.SimpleValue;

import java.util.Arrays;
import java.util.List;

/**
 * Предикат, получающий первым аргументом максимальное число,
 * генерирующий случайное число от 0 до максимального
 * и унифицирующий случайное число со вторым аргументом
*/
public class RandomPredicate extends AbstractPredicate {
    public RandomPredicate(TypeStorage typeStorage) {
        super("random", Arrays.asList("integer", "integer"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        //Получение из аргументов максимального числа
        Integer max = (Integer)args.get(0).getValue();

        //Исключение, если первый аргумент (максимальное число) - свободная переменная
        if(max==null) throw new FreeVariableException((Variable) args.get(0));
        //Если максимальное число отрицательное, fail
        if (max < 0) return PredicateResult.FAIL;

        //Генерация случайного числа
        int randInt = (int)(Math.random()*max);

        //Создание объекта значения со случайным числом
        SimpleValue randValue = new SimpleValue(typeStorage.get("integer"), randInt);

        //Унификация полученного значения со вторым аргументом.
        //Если унификация прошла успешно, возвращается 0; иначе -1(fail)
        return args.get(1).unify(randValue)
                ? PredicateResult.LAST_RESULT
                : PredicateResult.FAIL;
    }
}

