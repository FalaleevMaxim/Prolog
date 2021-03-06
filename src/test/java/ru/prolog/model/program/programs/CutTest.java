package ru.prolog.model.program.programs;


import ru.prolog.model.predicate.RuleExecutorPredicate;
import ru.prolog.model.program.Program;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.model.values.SimpleValueModel;
import ru.prolog.model.values.VariableModel;

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