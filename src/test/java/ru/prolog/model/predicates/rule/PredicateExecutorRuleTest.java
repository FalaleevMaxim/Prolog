package ru.prolog.model.predicates.rule;

import org.junit.Test;
import ru.prolog.model.Type;
import ru.prolog.context.predicate.BasePredicateContext;
import ru.prolog.model.predicate.RuleExecutorPredicate;
import ru.prolog.std.WritePredicate;
import ru.prolog.values.SimpleValue;
import ru.prolog.values.Value;
import ru.prolog.values.variables.SimpleVariable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicateExecutorRuleTest {
    Type string = Type.getType("string");

    @Test
    public void helloWorldTest(){
        new BasePredicateContext(
                new WritePredicate(),
                getArg("Hello, world"))
                .execute();
    }

    private List<Value> getArg(String arg) {
        return Collections.singletonList(
                new SimpleValue(string, arg));
    }

    @Test
    public void factTest(){
        RuleExecutorPredicate predicate = new RuleExecutorPredicate("student", Collections.singletonList("string"));
        Stream.of("Vasya", "Petya", "Masha").forEach(s ->
                    predicate.addRule(
                            new Rule(predicate,
                                    getArg(s))));
        assertTrue(new BasePredicateContext(predicate, getArg("Masha")).execute());
        assertFalse(new BasePredicateContext(predicate, getArg("Kolya")).execute());

        SimpleVariable x = new SimpleVariable(string, "X");
        new BasePredicateContext(predicate, Collections.singletonList(x)).execute();
        assertEquals("Vasya", x.getValue());
    }
}