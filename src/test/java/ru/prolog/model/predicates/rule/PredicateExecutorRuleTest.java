package ru.prolog.model.predicates.rule;

import org.junit.Test;
import ru.prolog.model.Type;
import ru.prolog.model.predicates.execution.predicate.BasePredicateExecution;
import ru.prolog.model.predicates.predicate.RuleExecutorPredicate;
import ru.prolog.model.predicates.predicate.std.WritePredicate;
import ru.prolog.model.values.PrologList;
import ru.prolog.model.values.SimpleValue;
import ru.prolog.model.values.Value;
import ru.prolog.model.values.variables.ListVariable;
import ru.prolog.model.values.variables.SimpleVariable;

import java.util.Arrays;
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
        new BasePredicateExecution(
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
        assertTrue(new BasePredicateExecution(predicate, getArg("Masha")).execute());
        assertFalse(new BasePredicateExecution(predicate, getArg("Kolya")).execute());

        SimpleVariable x = new SimpleVariable(string, "X");
        new BasePredicateExecution(predicate, Collections.singletonList(x)).execute();
        assertEquals("Vasya", x.getValue());
    }
}