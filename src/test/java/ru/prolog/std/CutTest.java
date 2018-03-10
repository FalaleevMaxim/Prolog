package ru.prolog.std;

import org.junit.Assert;
import org.junit.Test;
import ru.prolog.model.Type;
import ru.prolog.context.predicate.BasePredicateContext;
import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.RuleExecutorPredicate;
import ru.prolog.model.predicates.rule.StatementExecutorRule;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.predicates.rule.Statement;
import ru.prolog.values.AnonymousVariable;
import ru.prolog.values.SimpleValue;

import java.util.Arrays;
import java.util.Collections;


/*
predicates
cutTest(string)
clauses
cutTest("Not"):-!,fail.
cutTest(_).

 */
public class CutTest {
    @Test
    public void cutTest(){
        Type string = Type.getType("string");
        RuleExecutorPredicate predicate = new RuleExecutorPredicate("cutTest", Collections.singletonList("string"));
        StatementExecutorRule rule1 = new StatementExecutorRule(predicate,
                Collections.singletonList(new SimpleValue(string,"Not")),
                Arrays.asList(
                        new Statement(new Cut(),Collections.emptyList()),
                        new Statement(new Fail(),Collections.emptyList())));
        Rule rule2 = new Rule(predicate, Collections.singletonList(new AnonymousVariable(string)));
        predicate.addRule(rule1);
        predicate.addRule(rule2);

        PredicateContext execution = new BasePredicateContext(predicate, Collections.singletonList(new SimpleValue(string,"Not")));
        Assert.assertFalse(execution.execute());

        execution = new BasePredicateContext(predicate, Collections.singletonList(new SimpleValue(string,"Other")));
        Assert.assertTrue(execution.execute());
    }
}