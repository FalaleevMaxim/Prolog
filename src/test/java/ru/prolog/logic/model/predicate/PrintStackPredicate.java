package ru.prolog.logic.model.predicate;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.values.Value;
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
        printStack(context.getRuleContext(), out);
        out.println(getName());
        return 0;
    }

    private void printStack(RuleContext ctx, OutputDevice out){
        if(ctx==null) return;
        printStack(ctx.getPredicateContext().getRuleContext(), out);
        out.println(ctx.getRule().toString());
        out.println(ToStringUtil.funcToString(ctx.getPredicateContext().predicate().getName(), ctx.getArgs()));
    }
}
