package ru.prolog.model.program.programs;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.program.Program;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.type.Type;
import ru.prolog.values.model.ValueModel;
import ru.prolog.values.simple.SimpleValue;
import ru.prolog.values.simple.SimpleValueModel;

import java.util.Collections;

public class HelloWorld implements TestProgram {
    private Program program;

    public HelloWorld() {
        //Создание объекта программы
        program = new Program();
        //Получение типа string
        Type string = program.domains().get("string");
        //Создаётся значение типа string содержащее текст "Hello world"
        ValueModel helloStr = new SimpleValueModel(string, "Hello world");
        //Получение стандартного предиката "write" из программы. Самый простой способ - использовать метод getFitting.
        //Он ищет сначала метод по точному совпадению типов, затем vararg. Предикат write принимает vararg (неограниченное количество аргументов)
        Predicate writePred = program.predicates().getFitting("write", Collections.singletonList(string));
        //Создание выражения write("Hello world")
        Statement writeStat = new Statement(writePred, Collections.singletonList(helloStr));
        //Добавление выражения в цель программы
        program.goal().addStatement(writeStat);
        //Закрепить состояние программы (проверяет ошибки и делает всю модель неизменяемой если ошибок нет)
        program.fix();
    }

    @Override
    public Program getProgram() {
        return program;
    }
}
