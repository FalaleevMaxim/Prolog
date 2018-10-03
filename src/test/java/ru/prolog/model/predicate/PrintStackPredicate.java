package ru.prolog.model.predicate;

import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.util.ToStringUtil;
import ru.prolog.util.io.OutputDevice;

import java.util.List;

public class PrintStackPredicate extends AbstractPredicate {

    public PrintStackPredicate() {
        super("printStack");
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        OutputDevice out = context.programContext().getOutputDevices();
        printStack(context.ruleContext(), out);
        out.println(getName());
        return PredicateResult.LAST_RESULT;
    }

    private void printStack(RuleContext ctx, OutputDevice out){
        if(ctx==null) return;
        printStack(ctx.getPredicateContext().ruleContext(), out);
        out.println(ctx.rule().toString());
        out.println(ToStringUtil.funcToString(ctx.getPredicateContext().predicate().getName(), ctx.getArgs()));
    }
}
