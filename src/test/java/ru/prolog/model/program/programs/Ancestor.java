package ru.prolog.model.program.programs;

import ru.prolog.model.predicate.RuleExecutorPredicate;
import ru.prolog.model.program.Program;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.model.type.Type;
import ru.prolog.model.values.SimpleValueModel;
import ru.prolog.model.values.VariableModel;

import java.util.Arrays;
import java.util.Collections;

/**
 * domains
 *  имя = string
 * predicates
 *  родитель(имя, имя)
 *  предок(имя, имя)
 * clauses
 *  предок(X,Y):-родитель(X,Y).
 *  предок(X,Y):-родитель(X,Z), предок(Z,Y).
 *  родитель("Юрий", "Дмитрий").
 *  родитель("Александр", "Алексей").
 *  родитель("Алексей", "Данил").
 *  родитель("Алексей", "Кирилл").
 *  родитель("Алексей", "Борис").
 * goal
 *  предок("Александр", X), write(X),nl,fail.
 *  предок(X, "Борис"), write(X),nl,fail.
 */
public class Ancestor implements TestProgram{
    private Program program = new Program();

    public Ancestor(boolean valueFirst) {
        domains();
        predicates();
        clauses();
        goal(valueFirst);
    }

    /**
     * domains
     *  имя = string
     */
    private void domains(){
        program.domains().addType("имя", program.domains().get("string"));
    }

    /**
     * predicates
     *  однофамилец(человек, человек)
     *  родитель(имя, имя)
     *  предок(имя, имя)
     */
    private void predicates(){
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
        parent_rule();
        ancestor_rule();
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
    private void goal(boolean setFirst){
        Type name = program.domains().get("имя");
        //предок("Александр", X) или предок(X, "Борис")
        program.goal().addStatement(
                new Statement(
                        program.predicates().get("предок", 2),
                        setFirst?
                                Arrays.asList(
                                        new SimpleValueModel(name, "Александр"),
                                        new VariableModel(name, "X")):
                                Arrays.asList(
                                        new VariableModel(name, "X"),
                                        new SimpleValueModel(name, "Борис"))));
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
