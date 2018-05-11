package ru.prolog.logic.model.program.programs;

import ru.prolog.logic.context.rule.DebuggerRuleContextDecorator;
import ru.prolog.logic.model.predicate.RuleExecutorPredicate;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.logic.model.rule.StatementExecutorRule;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.type.descriptions.CompoundType;
import ru.prolog.logic.model.type.descriptions.FunctorType;
import ru.prolog.logic.values.model.FunctorValueModel;
import ru.prolog.logic.values.model.VariableModel;
import ru.prolog.logic.values.simple.SimpleValueModel;

import java.util.Arrays;
import java.util.Collections;

/**
 * domains
 *  человек = чел(имя, фамилия)
 *  имя = string
 *  фамилия = string
 * predicates
 *  однофамилец(человек, человек)
 *  родитель(имя, имя)
 *  предок(имя, имя)
 * clauses
 *  однофамилец(чел(_,F),чел(_,F)).
 *  предок(X,Y):-родитель(X,Y).
 *  предок(X,Y):-родитель(X,Z), предок(Z,Y).
 *  родитель("Юрий", "Дмитрий").
 *  родитель("Александр", "Алексей").
 *  родитель("Алексей", "Данил").
 *  родитель("Алексей", "Кирилл").
 *  родитель("Алексей", "Борис").
 * goal
 *  предок("Александр", X), write(X),nl,fail.

 */
public class Ancestor implements TestProgram{
    private Program program = new Program();

    public Ancestor() {
        domains();
        predicates();
        clauses();
        goal();
    }

    /**
     * domains
     *  человек = чел(имя, фамилия)
     *  имя = string
     *  фамилия = string
     */
    private void domains(){
        CompoundType humanTypeDef = new CompoundType();
        FunctorType humanFunc = new FunctorType("чел", Arrays.asList("имя", "фамилия"));
        humanTypeDef.addFunctor(humanFunc);
        program.domains().addType("человек", new Type(humanTypeDef));
        program.domains().addType("имя", program.domains().get("string"));
        program.domains().addType("фамилия", program.domains().get("string"));
    }

    /**
     * predicates
     *  однофамилец(человек, человек)
     *  родитель(имя, имя)
     *  предок(имя, имя)
     */
    private void predicates(){
        program.predicates().add(new RuleExecutorPredicate("однофамилец", Arrays.asList("человек", "человек"), program.domains()));
        program.predicates().add(new RuleExecutorPredicate("родитель", Arrays.asList("имя", "имя"), program.domains()));
        program.predicates().add(new RuleExecutorPredicate("предок", Arrays.asList("имя", "имя"), program.domains()));
    }

    /**
     * clauses
     *  однофамилец(чел(_,F),чел(_,F)).
     *  предок(X,Y):-родитель(X,Y).
     *  предок(X,Y):-родитель(X,Z), предок(Z,Y).
     *  родитель("Юрий", "Дмитрий").
     *  родитель("Александр", "Алексей").
     *  родитель("Алексей", "Данил").
     *  родитель("Алексей", "Кирилл")).
     */
    private void clauses(){
        namesake_rule();
        parent_rule();
        ancestor_rule();
    }

    /**
     *  однофамилец(чел(_,F),чел(_,F)).
     */
    private void namesake_rule(){
        StatementExecutorRule namesakeRule = new StatementExecutorRule();
        VariableModel anonVar = new VariableModel(program.domains().get("имя"), "_");
        VariableModel var_F = new VariableModel(program.domains().get("фамилия"), "Ф");
        VariableModel var_F2 = new VariableModel(program.domains().get("фамилия"), "Ф");
        FunctorValueModel human1 = new FunctorValueModel(program.domains().get("человек"), "чел", Arrays.asList(anonVar, var_F));
        FunctorValueModel human2 = new FunctorValueModel(program.domains().get("человек"), "чел", Arrays.asList(anonVar, var_F2));
        namesakeRule.addUnifyArg(human1);
        namesakeRule.addUnifyArg(human2);
        ((RuleExecutorPredicate)program.predicates().get("однофамилец",2)).addRule(namesakeRule);
    }

    /**
     *  //родитель("Юрий", "Дмитрий").
     *  родитель("Александр", "Алексей").
     *  родитель("Алексей", "Данил").
     *  родитель("Алексей", "Кирилл").
     *  родитель("Кирилл", "Борис").
     */
    private void parent_rule(){
        Type name = program.domains().get("имя");
        RuleExecutorPredicate parent = (RuleExecutorPredicate)program.predicates().get("родитель",2);
        StatementExecutorRule parentRule = new StatementExecutorRule(Arrays.asList(
                new SimpleValueModel(name, "Юрий"),
                new SimpleValueModel(name, "Дмитрий")));
        parent.addRule(parentRule);
        parentRule = new StatementExecutorRule(Arrays.asList(
                new SimpleValueModel(name, "Александр"),
                new SimpleValueModel(name, "Алексей")));
        parent.addRule(parentRule);
        parentRule = new StatementExecutorRule(Arrays.asList(
                new SimpleValueModel(name, "Алексей"),
                new SimpleValueModel(name, "Данил")));
        parent.addRule(parentRule);
        parentRule = new StatementExecutorRule(Arrays.asList(
                new SimpleValueModel(name, "Алексей"),
                new SimpleValueModel(name, "Кирилл")));
        parent.addRule(parentRule);
        parentRule = new StatementExecutorRule(Arrays.asList(
                new SimpleValueModel(name, "Кирилл"),
                new SimpleValueModel(name, "Борис")));
        parent.addRule(parentRule);
    }

    /**
     *  предок(X,Y):-родитель(X,Y).
     *  предок(X,Y):-родитель(X,Z), предок(Z,Y).
     */
    private void ancestor_rule(){
        Type name = program.domains().get("имя");
        RuleExecutorPredicate ancestor = (RuleExecutorPredicate)program.predicates().get("предок",2);
        StatementExecutorRule ancestorRule = new StatementExecutorRule(Arrays.asList(
                new VariableModel(name, "X"),
                new VariableModel(name, "Y")));
        ancestorRule.addStatement(
                new Statement(
                    program.predicates().get("родитель", 2),
                    Arrays.asList(
                        new VariableModel(name, "X"),
                        new VariableModel(name, "Y"))));
        ancestor.addRule(ancestorRule);

        StatementExecutorRule ancestorRule2 = new StatementExecutorRule(Arrays.asList(
                new VariableModel(name, "X"),
                new VariableModel(name, "Y")));
        ancestorRule2.addStatement(
                new Statement(
                        program.predicates().get("родитель", 2),
                        Arrays.asList(
                                new VariableModel(name, "X"),
                                new VariableModel(name, "Z"))));
        ancestorRule2.addStatement(
                new Statement(
                        program.predicates().get("предок", 2),
                        Arrays.asList(
                                new VariableModel(name, "Z"),
                                new VariableModel(name, "Y"))));
        ancestor.addRule(ancestorRule2);
    }

    /**
     * goal
     *  предок("Александр", X), write(X),nl,fail.
     */
    private void goal(){
        Type name = program.domains().get("имя");
        //предок("Александр", X)
        program.goal().addStatement(
                new Statement(
                        program.predicates().get("предок", 2),
                        Arrays.asList(
                                new SimpleValueModel(name, "Александр"),
                                new VariableModel(name, "X"))));
        //write(X)
        program.goal().addStatement(
                new Statement(
                        program.predicates().getVarArgPredicate("write"),
                        Collections.singletonList(
                                new VariableModel(name, "X"))));
        //nl
        program.goal().addStatement(new Statement(
                program.predicates().get("nl",0),
                Collections.emptyList()));
        //fail
        program.goal().addStatement(new Statement(
                program.predicates().get("fail",0),
                Collections.emptyList()));
    }

    @Override
    public Program getProgram() {
        return program;
    }
}
