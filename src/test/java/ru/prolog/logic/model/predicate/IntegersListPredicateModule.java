package ru.prolog.logic.model.predicate;

import ru.prolog.logic.model.program.ProgramModule;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.list.ListValue;
import ru.prolog.logic.runtime.values.list.PrologList;
import ru.prolog.logic.storage.predicates.SinglePredicateStorage;
import ru.prolog.logic.storage.type.TypeStorageImpl;

import java.util.Arrays;
import java.util.List;

public class IntegersListPredicateModule extends ProgramModule {
    public IntegersListPredicateModule() {
        typeStorage = new TypeStorageImpl();
        //Добавление списка чисел intList в хранилище типов
        typeStorage.addType("intList", new Type(typeStorage.get("integer"), "integer"));
        //Создание хранилища предикатов для одного предиката
        predicateStorage = new SinglePredicateStorage(
                //Наследование класса предиката от AbstractPredicate.
                // Создаваемый предикат имеет имя intsAsList, принимает список чисел
                // и произвольное количество чисел, которые приведёт к списку и унифицирует с 1 аргументом
                new AbstractPredicate("intsAsList", Arrays.asList("intList","..."), typeStorage) {
            @Override
            public int run(PredicateContext context, List<Value> args, int startWith) {
                //При бэктрекинге нет других решений
                if(startWith>0) return -1;
                //Если аргумент всего один, унифицировать его с пустым списком
                if(args.size()==1){
                    return new ListValue(typeStorage.get("intList")).unify(args.get(0)) ? 0 : 1;
                }
                //Сбор будущих элементов списка
                Value[] elements = new Value[args.size()-1];
                for (int i = 1; i < args.size(); i++) {
                    elements[i-1] = args.get(i);
                }
                //Унификация первого аргумента со списком
                return PrologList.asList(typeStorage.get("intList"), elements).unify(args.get(0))?0:-1;
            }
        });
    }
}
