package ru.prolog.logic.model.program.programs;

import ru.prolog.logic.model.predicate.RuleExecutorPredicate;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.logic.model.rule.StatementExecutorRule;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.values.ListValueModel;
import ru.prolog.logic.model.values.VariableModel;
import ru.prolog.logic.model.values.SimpleValueModel;

import java.util.Arrays;
import java.util.Collections;

/**
 domains
 lst=integer*
 predicates
 conc(lst,lst,lst)
 clauses
 conc([],L,L).
 conc([H|L1],L2,[H|R]):-conc(L1,L2,R).
 goal
 conc(L1,L2,[1,2,3,4,5]),write(L1,L2),nl,fail.
 */
public class Conc3 implements TestProgram {
    private Program program = new Program();

    public Conc3() {
        Type integer = program.domains().get("integer");
        Type listType = new Type(integer, "integer");
        program.domains().addType("lst", listType);

        RuleExecutorPredicate conc = new RuleExecutorPredicate("conc", Arrays.asList("lst", "lst", "lst"), program.domains());
        program.predicates().add(conc);

        StatementExecutorRule concRule1 = new StatementExecutorRule(Arrays.asList(
                new ListValueModel(listType),
                new VariableModel(listType, "L"),
                new VariableModel(listType, "L")));
        conc.addRule(concRule1);

        StatementExecutorRule concRule2 = new StatementExecutorRule(Arrays.asList(
                new ListValueModel(
                        listType,
                        new VariableModel(listType, "L1"),
                        new VariableModel(integer, "H")),
                new VariableModel(listType, "L2"),
                new ListValueModel(
                        listType,
                        new VariableModel(listType, "R"),
                        new VariableModel(integer, "H"))));

        concRule2.addStatement(new Statement(conc, Arrays.asList(
                new VariableModel(listType, "L1"),
                new VariableModel(listType, "L2"),
                new VariableModel(listType, "R"))));
        conc.addRule(concRule2);

        program.goal().addStatement(new Statement(conc, Arrays.asList(
                new VariableModel(listType, "L1"),
                new VariableModel(listType, "L2"),
                new ListValueModel(listType,
                        new SimpleValueModel(integer, 1),
                        new SimpleValueModel(integer, 2),
                        new SimpleValueModel(integer, 3),
                        new SimpleValueModel(integer, 4),
                        new SimpleValueModel(integer, 5)))));
        program.goal().addStatement(new Statement(
                program.predicates().getVarArgPredicate("write"),
                Arrays.asList(
                        new VariableModel(listType, "L1"),
                        new VariableModel(listType, "L2"))));
        program.goal().addStatement(new Statement(
                program.predicates().get("nl",0),
                Collections.emptyList()));
        program.goal().addStatement(new Statement(
                program.predicates().get("fail",0),
                Collections.emptyList()));
    }

    @Override
    public Program getProgram() {
        return program;
    }
}
