package ru.prolog.model.predicate;

import org.junit.Test;
import ru.prolog.model.Type;
import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.rule.BaseRuleContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.std.Cut;
import ru.prolog.std.Fail;
import ru.prolog.std.Nl;
import ru.prolog.std.WritePredicate;
import ru.prolog.model.predicates.rule.StatementExecutorRule;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.predicates.rule.Statement;
import ru.prolog.values.*;
import ru.prolog.values.variables.ListVariable;
import ru.prolog.values.variables.SimpleVariable;
import ru.prolog.values.variables.Variable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RuleExecutorPredicateTest {
    private Type integer = Type.getType("integer");
    private Type list;
    private RuleExecutorPredicate concPredicate;
    private RuleExecutorPredicate notEqualPredicate;
    private StatementExecutorRule goal;


    //domains
    //lst=integer*
    //predicates
    //conc(lst,lst,lst)
    //neq(lst,lst)
    //clauses
    //conc([],L,L).
    //conc([H|L1],L2,[H|R]):-conc(L1,L2,R).
    //neq(L,L):-!,fail.
    //neq(_,_).
    @Test
    public void concTest(){
        domains();
        predicates();
        clauses();

        System.out.println(">conc([1,2,3],[4,5,6],R), write(R), nl.");
        goal1();
        RuleContext goal1 = new BaseRuleContext(goal, Collections.emptyList());
        assertTrue(goal1.execute());

        System.out.println(">conc([1,2,3],R,[1,2,3,4,5,6]), write(R), nl.");
        goal2();
        RuleContext goal2 = new BaseRuleContext(goal, Collections.emptyList());
        assertTrue(goal2.execute());

        System.out.println(">conc(R,[4,5,6],[1,2,3,4,5,6]), write(R), nl.");
        goal3();
        RuleContext goal3 = new BaseRuleContext(goal, Collections.emptyList());
        assertTrue(goal3.execute());

        System.out.println(">conc(L1,L2,[1,2,3,4,5,6]), write(L1), write(L2), nl, neq(L1,[]), neq(L1,[1]).");
        goal4();
        RuleContext goal4 = new BaseRuleContext(goal, Collections.emptyList());
        assertTrue(goal4.execute());
    }

    @Test
    public void twoVarConcTest(){
        domains();
        predicates();
        clauses();
        System.out.println(">conc(L1,L2,[1,2,3]), write(L1), nl, fail.");
        goal_2v();
        RuleContext goal_2v = new BaseRuleContext(goal, Collections.emptyList());
        assertTrue(goal_2v.execute());
    }

    private void domains(){
        //domains
        //lst=integer*
        list = Type.listType("lst", integer);
    }

    //predicates
    //conc(lst,lst,lst)
    private void predicates(){
        class ConcPredicate extends RuleExecutorPredicate{

            ConcPredicate() {
                super("conc", Arrays.asList("lst", "lst", "lst"));
            }

            @Override
            public int run(PredicateContext context, List<Value> args, int startWith) {
                return super.run(context, args, startWith);
            }
        }

        concPredicate = new ConcPredicate();
        notEqualPredicate = new RuleExecutorPredicate("neq", Arrays.asList("lst", "lst"));
    }

    // clauses
    // conc([],L,L).
    // conc([H|L1],L2,[H|R]):-conc(L1,L2,R).
    private void clauses(){
        conc_rule1();
        conc_rule2();
        neq_rule1();
        neq_rule2();
    }

    //conc([],L,L).
    private void conc_rule1(){
        PrologList emptyList = new ListValue(list);//[]
        ListVariable var_L = new ListVariable(list,"L");//L
        class ConcRule1 extends Rule{
            ConcRule1() {
                super(concPredicate, Arrays.asList(emptyList, var_L, var_L));
            }

            @Override
            protected boolean body(RuleContext context) {
                return super.body(context);
            }
        }

        Rule concRule1 = new ConcRule1();
        concPredicate.addRule(concRule1);
    }

    //conc([H|L1],L2,[H|R]):-conc(L1,L2,R).
    private void conc_rule2(){
        Variable var_H = new SimpleVariable(integer, "H");//H
        ListVariable var_L1 = new ListVariable(list,"L1");//L1
        ListVariable var_L2 = new ListVariable(list,"L2");//L2
        ListVariable var_R = new ListVariable(list,"R");//R
        PrologList l1 = PrologList.asList(var_L1, var_H); //[H|L1]
        PrologList l2 = PrologList.asList(var_R, var_H); //[H|R]

        class ConcRule2 extends StatementExecutorRule{
            public ConcRule2() {
                super(concPredicate, Arrays.asList(l1, var_L2, l2),//conc([H|L1],L2,[H|R])
                    Collections.singletonList(  //:-
                            new Statement(concPredicate, Arrays.asList(var_L1, var_L2, var_R))));//conc(L1,L2,R).
            }

            @Override
            protected boolean body(RuleContext context) {
                return super.body(context);
            }
        }

        StatementExecutorRule concRule2 = new ConcRule2();
        concPredicate.addRule(concRule2);
    }

    //neq(L,L):-!,fail.
    private void neq_rule1(){
        Variable var_L = new ListVariable(list, "L");
        List<Value> noArgs = Collections.emptyList();
        Rule neq_rule = new StatementExecutorRule(notEqualPredicate, Arrays.asList(var_L, var_L),
                Arrays.asList(
                        new Statement(new Cut(), noArgs),
                        new Statement(new Fail(), noArgs)));
        notEqualPredicate.addRule(neq_rule);
    }

    //neq(_,_).
    private void neq_rule2(){
        Value anon = new AnonymousVariable(list);
        Rule neq_rule = new Rule(notEqualPredicate, Arrays.asList(anon, anon));
        notEqualPredicate.addRule(neq_rule);
    }


    /*
    goal
    conc([1,2,3],[4,5,6],R), write(R), nl.
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

        goal = new StatementExecutorRule(null, Collections.emptyList(), Arrays.asList(conc, write, Nl.statement()));
    }

    /*
    goal
    conc([1,2,3],R,[1,2,3,4,5,6]), write(R), nl.
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

        goal = new StatementExecutorRule(null, Collections.emptyList(), Arrays.asList(conc, write, Nl.statement()));
    }

    /*
    goal
    conc(R,[4,5,6],[1,2,3,4,5,6]), write(R), nl.
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

        goal = new StatementExecutorRule(null, Collections.emptyList(), Arrays.asList(conc, write, Nl.statement()));
    }

    /*
    goal
    conc(L1,L2,[1,2,3,4,5,6]), write(L1), write(L2), nl, neq(L1,[]), neq(L1,[1]).
     */
    private void goal4(){
        ListVariable var_L1 = new ListVariable(list, "L1");
        ListVariable var_L2 = new ListVariable(list, "L2");
        PrologList r = PrologList.asList(list,
                new SimpleValue(integer, 1),
                new SimpleValue(integer, 2),
                new SimpleValue(integer, 3),
                new SimpleValue(integer, 4),
                new SimpleValue(integer, 5),
                new SimpleValue(integer, 6));
        PrologList l1 = PrologList.asList(list, new SimpleValue(integer, 1));
        Statement conc = new Statement(concPredicate, Arrays.asList(var_L1, var_L2, r));

        Statement write1 = new Statement(new WritePredicate(), Collections.singletonList(var_L1));
        Statement write2 = new Statement(new WritePredicate(), Collections.singletonList(var_L2));

        Statement notEmpty = new Statement(notEqualPredicate, Arrays.asList(var_L1, new ListValue(list)));
        Statement notOne = new Statement(notEqualPredicate, Arrays.asList(var_L1, l1));

        goal = new StatementExecutorRule(null, Collections.emptyList(), Arrays.asList(conc, write1, write2, Nl.statement(), notEmpty, notOne));
    }


    private void goal_2v(){
        ListVariable var_L1 = new ListVariable(list, "L1");
        ListVariable var_L2 = new ListVariable(list, "L2");
        PrologList r = PrologList.asList(list,
                new SimpleValue(integer, 1),
                new SimpleValue(integer, 2),
                new SimpleValue(integer, 3));
        PrologList l1 = PrologList.asList(list, new SimpleValue(integer, 1));
        Statement conc = new Statement(concPredicate, Arrays.asList(var_L1, var_L2, r));

        Statement write1 = new Statement(new WritePredicate(), Collections.singletonList(var_L1));

        goal = new StatementExecutorRule(null, Collections.emptyList(), Arrays.asList(conc, write1, Nl.statement(), Fail.statement()));
    }
}