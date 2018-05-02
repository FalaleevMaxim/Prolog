package ru.prolog.model.program.programs;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.program.Program;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.type.Type;
import ru.prolog.values.Value;
import ru.prolog.values.model.ValueModel;
import ru.prolog.values.simple.SimpleValue;

import java.util.Collections;

public class HelloWorldProgram implements TestProgram {
    private Program program;

    public HelloWorldProgram() {
        //Создание объекта программы
        program = new Program();
        //Получение типа string
        Type string = program.getTypeStorage().get("string");
        //Создаётся значение типа string содержащее текст "Hello world"
        SimpleValue helloStr = new SimpleValue(string, "Hello world");
        //Получение стандартного предиката "write" из программы. Самый простой способ - использовать метод getFitting.
        //Он ищет сначала метод по точному совпадению типов, затем vararg. Предикат write принимает vararg (неограниченное количество аргументов)
        Predicate writePred = program.getPredicateStorage().getFitting("write", Collections.singletonList(string));
        //Создание выражения write("Hello world")
        Statement writeStat = new Statement(writePred, Collections.singletonList(helloStr));
        //Добавление выражения в цель программы
        program.getGoalRule().addStatement(writeStat);
        //Закрепить состояние программы (проверяет ошибки и делает всю модель неизменяемой если ошибок нет)
        program.fix();
    }

    @Override
    public Program getProgram() {
        return program;
    }
}
