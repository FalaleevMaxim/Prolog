package ru.prolog.model.predicate;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.etc.exceptions.model.predicate.PredicateStateException;
import ru.prolog.model.rule.Rule;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RuleExecutorPredicate extends AbstractPrologPredicate {
    private static final String KEY_LAST_RULE_CONTEXT = "lastExecutedRuleContext";

    public RuleExecutorPredicate(String name){
        super(name);
    }

    public RuleExecutorPredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        super(name, argTypes, typeStorage);
    }

    public RuleExecutorPredicate(String name, List<String> argTypes, List<Rule> rules, TypeStorage types) {
        super(name, argTypes, rules, types);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        if(!fixed) throw new IllegalStateException("Predicate state is not fixed. Call fix() before running it.");
        //Получение из контекста информации, откуда начать/продолжить выполнение
        int startWith = getRuleNumberToStart(context);
        RuleContext ruleContext = getLastExecutedRuleContext(context);
        //Продолжить выполнение последнего правила с предыдущего запуска (если он был)
        if(ruleContext != null){
            if (ruleContext.redo()) {
                //Если правило нашло ещё решение, вернуть успех (при следующих запусках возможно будут ещё результаты)
                //Никаких записей в контексте обновлять не нужно: выполненное правило и его номер не изменились
                return PredicateResult.NEXT_RESULT;
            } else {
                //Если правило не сработало, смещаем указатель на следующее
                startWith++;
            }
        }
        //Цикл по правилам начиная с указателя startWith
        for(int i=startWith; i<rules.size();i++){
            //Если в предыдущем правиле было отсечение, перебор останавливается.
            if (context.isCut()) return PredicateResult.FAIL;
            //Создание контекста для очередного правила
            ruleContext = context.getRuleManager().context(rules.get(i), args, context);
            //Запуск правила через контекст
            if(ruleContext.execute()){
                //Если правило выполнено успешно, сохранить его контекст и номер в контексте предиката и вернуть успех
                setLastExecutedRuleContext(context, ruleContext, i);
                return PredicateResult.NEXT_RESULT;
            }
        }
        //Если правила закончились, результат не найден, fail.
        return PredicateResult.FAIL;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = super.exceptions();
        //Проверка что у предиката есть правила
        if(rules.isEmpty()){
            exceptions.add(new PredicateStateException(this, "No rules defined for predicate "+this));
        }
        return exceptions;
    }

    /**
     * Сохраняет в контексте вызова предиката контекст последнего успешно выполненного правила.
     *
     * @param predicateContext Текущий контекст выполнения предикатаю
     * @param ruleContext      Контекст правила, которое выполнилось успешно, и его нужно сохранить.
     * @param ruleNumber       Номер успешно выполненного правила
     */
    private static void setLastExecutedRuleContext(PredicateContext predicateContext, RuleContext ruleContext, int ruleNumber) {
        setSuccessRule(predicateContext, ruleNumber);
        predicateContext.putContextData(KEY_LAST_RULE_CONTEXT, ruleContext);
    }

    /**
     * Возвращает контекст последнего успешно выполненного правила из контекста предиката.
     *
     * @param predicateContext контекст предиката, в котором ищется правило
     * @return контекст последнего успешно выполненного правила или {@code null} если это первый вызов
     */
    private static RuleContext getLastExecutedRuleContext(PredicateContext predicateContext) {
        return (RuleContext) predicateContext.getContextData(KEY_LAST_RULE_CONTEXT);
    }
}
