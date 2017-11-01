package ru.prolog.model.predicates.predicate;

import org.junit.Test;
import ru.prolog.model.Type;
import ru.prolog.model.predicates.execution.rule.BackupingRuleExecutionDecorator;
import ru.prolog.model.predicates.execution.rule.BaseRuleExecution;
import ru.prolog.model.predicates.execution.rule.RuleExecution;
import ru.prolog.model.predicates.predicate.std.WritePredicate;
import ru.prolog.model.predicates.rule.PredicateExecutorRule;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.predicates.rule.Statement;
import ru.prolog.model.values.ListValue;
import ru.prolog.model.values.PrologList;
import ru.prolog.model.values.SimpleValue;
import ru.prolog.model.values.variables.ListVariable;
import ru.prolog.model.values.variables.SimpleVariable;
import ru.prolog.model.values.variables.Variable;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class RuleExecutorPredicateTest {
    private Type integer = Type.getType("integer");
    private Type list;
    private RuleExecutorPredicate concPredicate;
    private PredicateExecutorRule goal;


    //domains
    //lst=integer*
    //predicates
    //conc(lst,lst,lst)
    //clauses
    //conc([],L,L).
    //conc([H|L1],L2,[H|R]):-conc(L1,L2,R).
    @Test
    public void concTest(){
        domains();
        predicates();
        clauses();


        goal1();
        RuleExecution goal1 = new BackupingRuleExecutionDecorator( new BaseRuleExecution(goal, Collections.emptyList()) );
        assertTrue(goal1.execute());

        goal2();
        RuleExecution goal2 = new BackupingRuleExecutionDecorator( new BaseRuleExecution(goal, Collections.emptyList()) );
        assertTrue(goal2.execute());

        goal3();
        RuleExecution goal3 = new BackupingRuleExecutionDecorator( new BaseRuleExecution(goal, Collections.emptyList()) );
        assertTrue(goal3.execute());
    }

    private void domains(){
        //domains
        //lst=integer*
        list = Type.listType("lst", integer);
    }

    //predicates
    //conc(lst,lst,lst)
    private void predicates(){
        concPredicate = new RuleExecutorPredicate("conc", Arrays.asList("lst", "lst", "lst"));
    }

    // clauses
    // conc([],L,L).
    // conc([H|L1],L2,[H|R]):-conc(L1,L2,R).
    private void clauses(){
        rule1();
        rule2();
    }

    //conc([],L,L).
    private void rule1(){
        PrologList emptyList = new ListValue(list);//[]
        ListVariable var_L = new ListVariable(list,"L");//L
        Rule concRule1 = new Rule(concPredicate, Arrays.asList(emptyList, var_L, var_L));
        concPredicate.addRule(concRule1);
    }

    //conc([H|L1],L2,[H|R]):-conc(L1,L2,R).
    private void rule2(){
        Variable var_H = new SimpleVariable(integer, "H");//H
        ListVariable var_L1 = new ListVariable(list,"L1");//L1
        ListVariable var_L2 = new ListVariable(list,"L2");//L2
        ListVariable var_R = new ListVariable(list,"R");//R
        PrologList l1 = PrologList.asList(var_L1, var_H); //[H|L1]
        PrologList l2 = PrologList.asList(var_R, var_H); //[H|R]
        PredicateExecutorRule concRule2 = new PredicateExecutorRule(concPredicate, Arrays.asList(l1, var_L2, l2), //conc([H|L1],L2,[H|R])
                Collections.singletonList(  //:-
                        new Statement(concPredicate, Arrays.asList(var_L1, var_L2, var_R)))); //conc(L1,L2,R).
        concPredicate.addRule(concRule2);
    }

    /*
    goal
    conc([1,2,3],[4,5,6],R), write(R).
     */
    private void goal1(){
        PrologList l1 = PrologList.asList(list,
                new SimpleValue(integer, 1),
                new SimpleValue(integer, 2),
                new SimpleValue(integer, 3));
        PrologList l2 = PrologList.asList(list,
                new SimpleValue(integer, 4),
                new SimpleValue(integer, 5),
                new SimpleValue(integer, 6));
        ListVariable var_R = new ListVariable(list, "R");
        Statement conc = new Statement(concPredicate, Arrays.asList(l1, l2, var_R));

        Statement write = new Statement(new WritePredicate(), Collections.singletonList(var_R));

        goal = new PredicateExecutorRule(null, Collections.emptyList(), Arrays.asList(conc, write));
    }

    /*
    goal
    conc([1,2,3],R,[1,2,3,4,5,6]), write(R).
     */
    private void goal2(){
        PrologList l1 = PrologList.asList(list,
                new SimpleValue(integer, 1),
                new SimpleValue(integer, 2),
                new SimpleValue(integer, 3));
        PrologList r = PrologList.asList(list,
                new SimpleValue(integer, 1),
                new SimpleValue(integer, 2),
                new SimpleValue(integer, 3),
                new SimpleValue(integer, 4),
                new SimpleValue(integer, 5),
                new SimpleValue(integer, 6));
        ListVariable var_R = new ListVariable(list, "R");
        Statement conc = new Statement(concPredicate, Arrays.asList(l1, var_R, r));

        Statement write = new Statement(new WritePredicate(), Collections.singletonList(var_R));

        goal = new PredicateExecutorRule(null, Collections.emptyList(), Arrays.asList(conc, write));
    }

    /*
    goal
    conc(R,[4,5,6],[1,2,3,4,5,6]), write(R).
     */
    private void goal3(){
        ListVariable var_R = new ListVariable(list, "R");
        PrologList l2 = PrologList.asList(list,
                new SimpleValue(integer, 4),
                new SimpleValue(integer, 5),
                new SimpleValue(integer, 6));
        PrologList r = PrologList.asList(list,
                new SimpleValue(integer, 1),
                new SimpleValue(integer, 2),
                new SimpleValue(integer, 3),
                new SimpleValue(integer, 4),
                new SimpleValue(integer, 5),
                new SimpleValue(integer, 6));
        Statement conc = new Statement(concPredicate, Arrays.asList(var_R, l2, r));

        Statement write = new Statement(new WritePredicate(), Collections.singletonList(var_R));

        goal = new PredicateExecutorRule(null, Collections.emptyList(), Arrays.asList(conc, write));
    }

}