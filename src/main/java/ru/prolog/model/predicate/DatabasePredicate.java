package ru.prolog.model.predicate;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.etc.exceptions.model.predicate.DbRuleIsNotFactException;
import ru.prolog.etc.exceptions.model.predicate.IllegalPredicateNameException;
import ru.prolog.model.rule.FactRule;
import ru.prolog.model.rule.Rule;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.type.descriptions.CompoundType;
import ru.prolog.model.type.descriptions.Functor;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.util.NameChecker;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DatabasePredicate extends AbstractPrologPredicate implements Functor {

    private static final String RULE_COUNT = "rule_count";

    protected DatabasePredicate(String name) {
        super(name);
    }

    public DatabasePredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        super(name, argTypes, typeStorage);
    }

    public DatabasePredicate(String name, List<String> argTypes, List<Rule> clauses, TypeStorage typeStorage) {
        super(name, argTypes, clauses, typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        if (!fixed) throw new IllegalStateException("Predicate state is not fixed. Call fix() before running it.");
        //Получение списка правил из базы данных для данного предиката
        List<FactRule> rules = context.programContext().database().getRules(this);
        //Получение номера правила, с которого нужно начать выполнение.
        int startWith = getRuleNumberToStart(context);
        //Получение количества правил и сохранённого в контексте количества правил при предыдущем вызове.
        int count = rules.size();
        Integer prev_count = (Integer) context.getContextData(RULE_COUNT);
        if (prev_count == null) prev_count = 0;
        //Если правил стало меньше, уменьшить соответственно номер текущего правила
        //ToDo: сделать более умный алгоритм, отслеживающий добавление/удаление правил
        if (count < prev_count) {
            startWith -= prev_count - count;
            if (startWith < 0) startWith = 0;
        }
        //Сохранение количества правил в контекст
        context.putContextData(RULE_COUNT, rules.size());
        //Цикл по правилам
        for (int i = startWith; i < rules.size(); i++) {
            //Создание контекста для очередного правила
            RuleContext ruleContext = context.getRuleManager().context(rules.get(i), args, context);
            if (ruleContext.execute()) {
                if (i == rules.size() - 1) {
                    //Если это правило последнее, вернуть LAST_RESULT
                    return PredicateResult.LAST_RESULT;
                } else {
                    //Иначе сохранить номер правила в контексте перед тем как вернуть NEXT_RESULT
                    setSuccessRule(context, i + 1);
                    return PredicateResult.NEXT_RESULT;
                }
            }
        }
        return PredicateResult.FAIL;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if (fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = super.exceptions();
        if (!NameChecker.canBePredicateName(name))
            exceptions.add(new IllegalPredicateNameException(this));
        for (Rule rule : rules) {
            if (!(rule instanceof FactRule))
                exceptions.add(new DbRuleIsNotFactException(this, rule));
        }
        return exceptions;
    }

    @Override
    public CompoundType getCompoundType() {
        return typeStorage.getDatabaseType().getCompoundType();
    }

    @Override
    public void setCompoundType(CompoundType type) {

    }
}