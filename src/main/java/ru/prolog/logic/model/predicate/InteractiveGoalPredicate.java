package ru.prolog.logic.model.predicate;

import ru.prolog.compiler.CompileException;
import ru.prolog.compiler.PrologCompiler;
import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.util.io.InputDevice;
import ru.prolog.util.io.OutputDevice;

import java.io.IOException;
import java.util.*;

public class InteractiveGoalPredicate extends GoalPredicate{
    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        OutputDevice out = (OutputDevice) context.programContext().getContextData(ProgramContext.KEY_OUTPUT_DEVICE);

        while (true) {
            try {
                out.println("\nWrite goal:");
                String str = ((InputDevice) context.programContext().getContextData(ProgramContext.KEY_INPUT_DEVICE)).readLine();
                Collection<CompileException> exceptions = new ArrayList<>();
                Rule goal = PrologCompiler.parseOuterGoal(context.programContext().program(), str, exceptions);
                if (goal == null) {
                    for (CompileException e : exceptions) {
                        System.err.println(e);
                    }
                    continue;
                }

                exceptions.addAll(goal.exceptions());
                if (!exceptions.isEmpty()) {
                    for (CompileException e : exceptions) {
                        System.err.println(e);
                    }
                    continue;
                }

                goal.fix();
                RuleContext ruleContext = context.getRuleManager().context(goal, Collections.emptyList(), context);
                boolean r = ruleContext.execute();
                if(r) printVariables(out, ruleContext);
                else out.println("No");
                while (r){
                    r = ruleContext.redo();
                    if(r) printVariables(out, ruleContext);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void printVariables(OutputDevice out, RuleContext ruleContext){
        for (Variable var : ruleContext.getVariables()) {
            if(var.isFree()){
                if(var.getRelated().isEmpty())
                    out.println(var.getName()+ "=_");
                else {
                    out.print(var.getName());
                    for (Variable rel : var.getRelated()) {
                        if(var.getRuleContext()==rel.getRuleContext()) {
                            out.print("=" + rel);
                        }
                    }
                }
            }else out.println(var.getName()+"="+var);
        }
    }
}