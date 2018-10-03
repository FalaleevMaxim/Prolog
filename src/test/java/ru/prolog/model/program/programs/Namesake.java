package ru.prolog.model.program.programs;

import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.predicate.RuleExecutorPredicate;
import ru.prolog.model.program.Program;
import ru.prolog.model.rule.FactRule;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.model.type.Type;
import ru.prolog.model.type.descriptions.CompoundType;
import ru.prolog.model.type.descriptions.FunctorType;
import ru.prolog.model.values.FunctorValueModel;
import ru.prolog.model.values.SimpleValueModel;
import ru.prolog.model.values.VariableModel;
import ru.prolog.std.Not;

import java.util.Arrays;
import java.util.Collections;


/**
 * domains
 *  человек = чел(имя, фамилия);имя(имя)
 *  имя = string
 *  фамилия = string
 * database
 *  человек(человек)
 * predicates
 *  однофамилец(человек, человек)
 *  тёзка(человек, человек)
 *  имя(человек, имя)
 * clauses
 *  однофамилец(чел(_,F),чел(_,F)).
 *  тёзка(X,Y):-имя(X,N),имя(Y,N).
 *  имя(чел(N,_),N).
 *  имя(имя(N),N).
 *  человек(чел("Иван", "Иванов")).
 *  человек(чел("Иван", "Фёдоров")).
 *  человек(чел("Дмитрий", "Иванов")).
 *  человек(чел("Александр", "Фёдоров")).
 *  человек(чел("Алексей", "Иванов")).
 * goal
 *  assert(человек(чел("Борис", "Иванов"))), человек(X), однофамилец(чел("Алексей", "Фёдоров"),X),write(X),nl,fail;
 *  тёзка(имя("Иван"), X), retract(человек(X)), write(X),nl,fail.
 *  retractAll(чел(_, "Иванов")), save("db.txt").
 */
public class Namesake implements TestProgram {
    private Program program = new Program();

    @Override
    public Program getProgram() {
        return program;
    }

    public Namesake(int goal) {
        domains();
        database();
        predicates();
        clauses();
        switch (goal){
            case 0:
                goal1();
                break;
            case 1:
                goal2();
                break;
            case 2:
                goal3();
                break;
        }
    }

    /**
     * domains
     *  человек = чел(имя, фамилия);имя(имя)
     *  имя = string
     *  фамилия = string
     */
    private void domains(){
        CompoundType humanTypeDef = new CompoundType();
        FunctorType humanFunc = new FunctorType("чел", Arrays.asList("имя", "фамилия"), program.domains());
        FunctorType nameFunc = new FunctorType("имя", Collections.singletonList("имя"), program.domains());
        humanTypeDef.addFunctor(humanFunc);
        humanTypeDef.addFunctor(nameFunc);
        program.domains().addType("человек", new Type(humanTypeDef));
        program.domains().addType("имя", program.domains().get("string"));
        program.domains().addType("фамилия", program.domains().get("string"));
    }

    /**
     * database
     *  человек(человек)
     */
    private void database(){
        DatabasePredicate predicate =
                new DatabasePredicate("человек", Collections.singletonList("человек"), program.domains());
        program.predicates().add(predicate);
        program.database().addPredicate(predicate);
        //Предикат бызы данных является функтором для использвания в качестве аргумента assert и retract.
        program.domains().addDatabasePredicate(predicate);
    }

    /**
     * predicates
     *  однофамилец(человек, человек)
     *  тёзка(человек, человек)
     *  имя(человек, имя)
     */
    private void predicates(){
        program.predicates().add(new RuleExecutorPredicate("однофамилец", Arrays.asList("человек", "человек"), program.domains()));
        program.predicates().add(new RuleExecutorPredicate("тёзка", Arrays.asList("человек", "человек"), program.domains()));
        program.predicates().add(new RuleExecutorPredicate("имя", Arrays.asList("человек", "имя"), program.domains()));
    }

    /**
     * clauses
     *  однофамилец(чел(_,F),чел(_,F)).
     *  тёзка(X,Y):-имя(X,N),имя(Y,N).
     *  имя(чел(N,_),N).
     *  имя(имя(N),N).
     *  человек(чел("Иван", "Иванов")).
     *  человек(чел("Иван", "Фёдоров")).
     *  человек(чел("Дмитрий", "Иванов")).
     *  человек(чел("Александр", "Фёдоров")).
     *  человек(чел("Алексей", "Иванов")).
     */
    private void clauses(){
        lastNamesake_rule();
        namesake_rule();
        name_rules();
        human_facts();
    }

    /**
     *  однофамилец(чел(_,Ф),чел(_,Ф)).
     */
    private void lastNamesake_rule(){
        StatementExecutorRule namesakeRule = new StatementExecutorRule();
        VariableModel anonVar = new VariableModel(program.domains().get("имя"), "_");
        VariableModel var_F = new VariableModel(program.domains().get("фамилия"), "F");
        VariableModel var_F2 = new VariableModel(program.domains().get("фамилия"), "F");
        FunctorValueModel human1 = new FunctorValueModel(program.domains().get("человек"), "чел", Arrays.asList(anonVar, var_F));
        FunctorValueModel human2 = new FunctorValueModel(program.domains().get("человек"), "чел", Arrays.asList(anonVar, var_F2));
        namesakeRule.addUnifyArg(human1);
        namesakeRule.addUnifyArg(human2);
        ((RuleExecutorPredicate)program.predicates().get("однофамилец",2)).addRule(namesakeRule);
    }

    /**
     * тёзка(X,Y):-имя(X,N),имя(Y,N).
     */
    private void namesake_rule(){
        StatementExecutorRule namesakeRule = new StatementExecutorRule(
                program.predicates().get("тёзка",2),
                Arrays.asList(
                        new VariableModel(program.domains().get("человек"), "X"),
                        new VariableModel(program.domains().get("человек"), "Y")));
        ((RuleExecutorPredicate)program.predicates().get("тёзка",2)).addRule(namesakeRule);
        //имя(X,N)
        namesakeRule.addStatement(new Statement(
                program.predicates().get("имя",2),
                Arrays.asList(
                        new VariableModel(program.domains().get("человек"), "X"),
                        new VariableModel(program.domains().get("имя"), "N"))));
        //имя(Y,N)
        namesakeRule.addStatement(new Statement(
                program.predicates().get("имя",2),
                Arrays.asList(
                        new VariableModel(program.domains().get("человек"), "Y"),
                        new VariableModel(program.domains().get("имя"), "N"))));
    }

    /**
     * имя(чел(N,_),N).
     * имя(имя(N),N).
     */
    private void name_rules(){
        //имя(чел(N,_),N).
        StatementExecutorRule nameRule = new StatementExecutorRule(
                program.predicates().get("имя",2),
                Arrays.asList(
                        new FunctorValueModel(
                                program.domains().get("человек"), "чел",
                                Arrays.asList(
                                        new VariableModel(program.domains().get("имя"), "N"),
                                        new VariableModel(program.domains().get("фамилия"), "_"))),
                        new VariableModel(program.domains().get("имя"), "N")));
        ((RuleExecutorPredicate)program.predicates().get("имя",2)).addRule(nameRule);

        //имя(имя(N),N).
        nameRule = new StatementExecutorRule(
                program.predicates().get("имя",2),
                Arrays.asList(
                        new FunctorValueModel(
                                program.domains().get("человек"), "имя",
                                Collections.singletonList(
                                        new VariableModel(program.domains().get("имя"), "N"))),
                        new VariableModel(program.domains().get("имя"), "N")));
        ((RuleExecutorPredicate)program.predicates().get("имя",2)).addRule(nameRule);
    }

    /**
     * человек(чел("Иван", "Иванов")).
     * человек(чел("Иван", "Фёдоров")).
     * человек(чел("Дмитрий", "Иванов")).
     * человек(чел("Александр", "Фёдоров")).
     * человек(чел("Алексей", "Иванов")).
     */
    private void human_facts(){
        human_fact("Иван", "Иванов");
        human_fact("Иван", "Фёдоров");
        human_fact("Дмитрий", "Иванов");
        human_fact("Александр", "Фёдоров");
        human_fact("Алексей", "Иванов");
    }

    private void human_fact(String name, String lastName){
        FactRule fact = new FactRule(
                program.predicates().get("человек", 1),
                Collections.singletonList(
                        new FunctorValueModel(
                                program.domains().get("человек"), "чел",
                                Arrays.asList(
                                        new SimpleValueModel(program.domains().get("имя"), name),
                                        new SimpleValueModel(program.domains().get("фамилия"), lastName)))));
        ((DatabasePredicate)program.predicates().get("человек", 1)).addRule(fact);
    }

    /**
     * assert(человек(чел("Борис", "Иванов"))), человек(X), однофамилец(чел("Олег", "Иванов"),X),write(X),nl,fail;
     */
    private void goal1(){
        program.goal().addStatement(new Statement(
                program.predicates().get("assert", 1),
                Collections.singletonList(
                        new FunctorValueModel(
                                program.domains().getDatabaseType(),
                                "человек",
                                Collections.singletonList(
                                        new FunctorValueModel(
                                                program.domains().get("человек"), "чел",
                                                Arrays.asList(
                                                        new SimpleValueModel(
                                                                program.domains().get("имя"),
                                                                "Борис"),
                                                        new SimpleValueModel(
                                                                program.domains().get("фамилия"),
                                                                "Иванов"))))))));
        program.goal().addStatement(new Statement(
                program.predicates().get("человек", 1),
                Collections.singletonList(
                        new VariableModel(program.domains().get("человек"), "X"))));
        program.goal().addStatement(new Statement(
                program.predicates().get("однофамилец", 2),
                Arrays.asList(
                        new FunctorValueModel(
                                program.domains().get("человек"), "чел",
                                Arrays.asList(
                                        new SimpleValueModel(
                                                program.domains().get("имя"),
                                                "Олег"),
                                        new SimpleValueModel(
                                                program.domains().get("фамилия"),
                                                "Иванов"))),
                        new VariableModel(program.domains().get("человек"), "X"))));
        writeX_nl_fail();
    }

    /**
     * человек(X), not(тёзка(имя("Иван"), X)), retract(человек(X)), write(X),nl,fail.
     */
    private void goal2(){
        program.goal().addStatement(new Statement(
                program.predicates().get("человек", 1),
                Collections.singletonList(
                        new VariableModel(program.domains().get("человек"), "X"))));
        program.goal().addStatement(new Statement(
                new Not(program.predicates().get("тёзка", 2)),
                Arrays.asList(
                        new FunctorValueModel(
                                program.domains().get("человек"), "имя",
                                Collections.singletonList(
                                        new SimpleValueModel(
                                                program.domains().get("имя"),
                                                "Иван"))),
                        new VariableModel(program.domains().get("человек"), "X"))));
        program.goal().addStatement(new Statement(
                program.predicates().get("retract", 1),
                Collections.singletonList(
                        new FunctorValueModel(
                                program.domains().getDatabaseType(),
                                "человек",
                                Collections.singletonList(
                                        new VariableModel(
                                                program.domains().get("человек"),
                                                "X"))))));
        writeX_nl_fail();
    }

    private void writeX_nl_fail() {
        program.goal().addStatement(new Statement(
                program.predicates().getVarArgPredicate("write"),
                Collections.singletonList(
                        new VariableModel(program.domains().get("человек"), "X"))));
        program.goal().addStatement(new Statement(
                program.predicates().get("nl", 0),
                Collections.emptyList()));
        program.goal().addStatement(new Statement(
                program.predicates().get("fail", 0),
                Collections.emptyList()));
    }

    /**
     * retractAll(человек(чел(_, "Иванов"))), save("db.txt").
     */
    private void goal3(){
        program.goal().addStatement(new Statement(
                program.predicates().get("retractAll", 1),
                Collections.singletonList(
                        new FunctorValueModel(
                                program.domains().getDatabaseType(),
                                "человек",
                                Collections.singletonList(
                                        new FunctorValueModel(
                                                program.domains().get("человек"), "чел",
                                                Arrays.asList(
                                                        new VariableModel(
                                                                program.domains().get("имя"),
                                                                "_"),
                                                        new SimpleValueModel(
                                                                program.domains().get("фамилия"),
                                                                "Иванов"))))))));
        program.goal().addStatement(new Statement(
                program.predicates().get("save",1),
                Collections.singletonList(
                        new SimpleValueModel(
                                program.domains().get("string"),
                                "db.txt"))));
    }
}
