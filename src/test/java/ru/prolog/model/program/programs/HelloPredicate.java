package ru.prolog.model.program.programs;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicate.RuleExecutorPredicate;
import ru.prolog.model.program.Program;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.model.type.Type;
import ru.prolog.values.model.ValueModel;
import ru.prolog.values.model.VariableModel;
import ru.prolog.values.simple.SimpleValue;
import ru.prolog.values.simple.SimpleValueModel;

import java.util.Arrays;
import java.util.Collections;

/**
 * predicates
 * hello(string)
 * clauses
 * hello(Name):-write("Hello, ", Name).
 * goal
 * hello("Prolog")
 */
public class HelloPredicate implements TestProgram {
    private Program program = new Program();

    public HelloPredicate() {
        //Создание предиката hello, принимающего 1 аргумент типа string.
        RuleExecutorPredicate helloPred = new RuleExecutorPredicate("hello", Collections.singletonList("string"), program.domains());
        //Добавление предиката в программу
        program.predicates().add(helloPred);

        //Создание правила для предиката
        StatementExecutorRule rule = new StatementExecutorRule();
        helloPred.addRule(rule);

        //Получение типа string
        Type string = program.domains().get("string");
        //Переменная Name типа string
        VariableModel var_Name = new VariableModel(string, "Name");
        //Переменная ставится в левую часть правила
        rule.addUnifyArg(var_Name);
        //Создаётся значение типа string содержащее текст "Hello, "
        ValueModel helloStr = new SimpleValueModel(string, "Hello, ");
        //Получение стандартного предиката "write" из программы.
        Predicate writePred = program.predicates().getFitting("write", Collections.singletonList(string));
        //Выражение write("Hello ", Name)
        Statement writeStat = new Statement(writePred, Arrays.asList(helloStr, var_Name));

        //Добавление выражения в правило
        rule.addStatement(writeStat);

        //Создаётся значение типа string содержащее текст "Prolog"
        ValueModel prologStr = new SimpleValueModel(string, "Prolog");
        //Выражение hello("Prolog")
        Statement helloStat = new Statement(helloPred, Collections.singletonList(prologStr));
        //Добавление выражения в цель.
        program.goal().addStatement(helloStat);

        program.fix();
    }

    @Override
    public Program getProgram() {
        return program;
    }
}
