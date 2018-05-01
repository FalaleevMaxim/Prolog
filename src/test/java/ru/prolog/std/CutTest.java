package ru.prolog.std;

import org.junit.Assert;
import org.junit.Test;
import ru.prolog.model.type.Type;
import ru.prolog.context.predicate.BasePredicateContext;
import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.RuleExecutorPredicate;
import ru.prolog.model.rule.*;
import ru.prolog.values.AnonymousVariable;
import ru.prolog.values.simple.SimpleValue;

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

    }
}