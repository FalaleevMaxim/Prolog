package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.exceprions.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.SimpleValue;
import ru.prolog.values.Value;
import ru.prolog.values.variables.Variable;

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
        //Унификация полуенного значения со вторым аргументом
        boolean res = args.get(1).unify(randValue);
        //Если унификация прошла успешно, возвращается следующий номер; иначе -1(fail)
        if(res) return 0;
        else return -1;
    }
}

