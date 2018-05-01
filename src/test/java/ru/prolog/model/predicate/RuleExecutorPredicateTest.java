package ru.prolog.model.predicate;

import org.junit.Test;
import ru.prolog.model.type.Type;
import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.rule.BaseRuleContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.rule.*;
import ru.prolog.std.Cut;
import ru.prolog.std.Fail;
import ru.prolog.std.Nl;
import ru.prolog.std.WritePredicate;
import ru.prolog.values.*;
import ru.prolog.values.list.ListValue;
import ru.prolog.values.list.PrologList;
import ru.prolog.values.AnonymousVariable;
import ru.prolog.values.list.ListVariable;
import ru.prolog.values.simple.SimpleValue;
import ru.prolog.values.simple.SimpleVariable;
import ru.prolog.values.Variable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RuleExecutorPredicateTest {
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
        /*domains();
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
        assertTrue(goal4.execute());*/
    }

    @Test
    public void twoVarConcTest(){
        /*domains();
        predicates();
        clauses();
        System.out.println(">conc(L1,L2,[1,2,3]), write(L1), nl, fail.");
        goal_2v();
        RuleContext goal_2v = new BaseRuleContext(goal, Collections.emptyList());
        assertTrue(goal_2v.execute());*/
    }
}