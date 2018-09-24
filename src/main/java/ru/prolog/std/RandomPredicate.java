package ru.prolog.std;

import ru.prolog.logic.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;
import ru.prolog.logic.runtime.values.simple.SimpleValue;
import ru.prolog.logic.storage.type.TypeStorage;

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
    public int run(PredicateContext context, List<Value> args, int startRule) {
        //fail при бэктрекинге позволят продолжить откат
        // и не допускает бесконечных циклов при fail после предиката
        if(startRule>0) return -1;

        //Получение из аргументов максимального числа
        Integer max = (Integer)args.get(0).getValue();

        //Исключение, если первый аргумент (максимальное число) - свободная переменная
        if(max==null) throw new FreeVariableException((Variable) args.get(0));
        //Если максимальное число отрицательное, fail
        if(max<0) return -1;

        //Генерация случайного числа
        int randInt = (int)(Math.random()*max);

        //Создание объекта значения со случайным числом
        SimpleValue randValue = new SimpleValue(typeStorage.get("integer"), randInt);

        //Унификация полученного значения со вторым аргументом.
        //Если унификация прошла успешно, возвращается 0; иначе -1(fail)
        return args.get(1).unify(randValue) ? 0 : -1;
    }
}

