package ru.prolog.logic.model.predicate;

import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.context.rule.RuleContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.util.ToStringUtil;
import ru.prolog.util.io.OutputDevice;

import java.util.List;

public class PrintStackPredicate extends AbstractPredicate {

    public PrintStackPredicate() {
        super("printStack");
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        OutputDevice out = context.programContext().getOutputDevices();
        printStack(context.ruleContext(), out);
        out.println(getName());
        return 0;
    }

    private void printStack(RuleContext ctx, OutputDevice out){
        if(ctx==null) return;
        printStack(ctx.getPredicateContext().ruleContext(), out);
        out.println(ctx.rule().toString());
        out.println(ToStringUtil.funcToString(ctx.getPredicateContext().predicate().getName(), ctx.getArgs()));
    }
}
