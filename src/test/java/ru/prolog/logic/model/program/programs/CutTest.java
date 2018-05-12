package ru.prolog.logic.model.program.programs;


import ru.prolog.logic.model.predicate.RuleExecutorPredicate;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.logic.model.rule.StatementExecutorRule;
import ru.prolog.logic.values.model.VariableModel;
import ru.prolog.logic.values.simple.SimpleValueModel;

import java.util.Arrays;
import java.util.Collections;

/**
 * predicates
 * cutTest(string)
 * clauses
 * cutTest("Not"):-!,fail.
 * cutTest(_).
 * goal
 * cutTest("Smth"), write(1), cutTest("Not"), write(2).
 */
public class CutTest implements TestProgram {
    private Program program = new Program();

    public CutTest() {
        program.predicates().add(
                new RuleExecutorPredicate(
                    "cutTest",
                    Collections.singletonList("string"),
                    Arrays.asList(
                            new StatementExecutorRule(
                                    Collections.singletonList(
                                            new SimpleValueModel(
                                                    program.domains().get("string"),
                                                    "Not")),
                                    Arrays.asList(
                                            new Statement(
                                                    program.predicates().get("!",0),
                                                    Collections.emptyList()),
                                            new Statement(
                                                    program.predicates().get("fail",0),
                                                    Collections.emptyList()))),
                            new StatementExecutorRule(
                                    Collections.singletonList(
                                            new VariableModel(
                                                    program.domains().get("string"),
                                                    "_")))),
                    program.domains()));
        program.goal().addStatement(
                new Statement(
                        program.predicates().get("cutTest",1),
                        Collections.singletonList(
                                new SimpleValueModel(
                                        program.domains().get("string"),
                                        "Smth"))));
        program.goal().addStatement(
                new Statement(
                        program.predicates().getVarArgPredicate("write"),
                        Collections.singletonList(
                                new SimpleValueModel(
                                        program.domains().get("integer"),
                                        1))));
        program.goal().addStatement(
                new Statement(
                        program.predicates().get("cutTest",1),
                        Collections.singletonList(
                                new SimpleValueModel(
                                        program.domains().get("string"),
                                        "Not"))));
        program.goal().addStatement(
                new Statement(
                        program.predicates().getVarArgPredicate("write"),
                        Collections.singletonList(
                                new SimpleValueModel(
                                        program.domains().get("integer"),
                                        2))));
    }

    @Override
    public Program getProgram() {
        return program;
    }
}