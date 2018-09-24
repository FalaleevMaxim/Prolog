package ru.prolog.logic.model.predicate;

import ru.prolog.compiler.CompileException;
import ru.prolog.compiler.PrologCompiler;
import ru.prolog.logic.etc.exceptions.runtime.ProgramInterruptedException;
import ru.prolog.logic.etc.exceptions.runtime.PrologRuntimeException;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.context.rule.RuleContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;
import ru.prolog.util.io.OutputDevice;

import java.io.IOException;
import java.util.*;

public class InteractiveGoalPredicate extends GoalPredicate{
    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        OutputDevice out = context.programContext().getOutputDevices();

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new ProgramInterruptedException();
            }
            try {
                out.println("\nWrite goal:");
                String str = context.programContext().getInputDevice().readLine();
                if(Thread.interrupted()){
                    Thread.currentThread().interrupt();
                    return -1;
                }
                if(str==null || str.equals("")) continue;

                Collection<CompileException> exceptions = new ArrayList<>();
                Rule goal = PrologCompiler.parseOuterGoal(context.programContext().program(), str, exceptions);
                if (goal == null) {
                    for (CompileException e : exceptions) {
                        context.programContext().getErrorListeners().println(e.toString());
                    }
                    continue;
                }

                exceptions.addAll(goal.exceptions());
                if (!exceptions.isEmpty()) {
                    for (CompileException e : exceptions) {
                        context.programContext().getErrorListeners().println(e.toString());
                    }
                    continue;
                }
                goal.fix();
                RuleContext ruleContext = context.getRuleManager().context(goal, Collections.emptyList(), context);
                boolean r;
                try {
                    r = ruleContext.execute();
                }catch (PrologRuntimeException e){
                    context.programContext().getErrorListeners().prologRuntimeException(e);
                    continue;
                }catch (RuntimeException e){
                    context.programContext().getErrorListeners().runtimeException(e);
                    continue;
                }
                int solutions = 0;
                boolean hasVars = false;
                if(r) {
                    hasVars = printVariables(out, ruleContext, ++solutions);
                }
                else out.println("No solutions");
                while (r){
                    try {
                        r = ruleContext.redo();
                    }catch (PrologRuntimeException e){
                        context.programContext().getErrorListeners().prologRuntimeException(e);
                        break;
                    }catch (RuntimeException e){
                        context.programContext().getErrorListeners().runtimeException(e);
                        break;
                    }
                    if(r){
                        if(hasVars || !ruleContext.getVariables().isEmpty())
                        hasVars = printVariables(out, ruleContext, ++solutions);
                    }
                }
                if(solutions>0) out.println("Found "+solutions+ " solutions");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean printVariables(OutputDevice out, RuleContext ruleContext, int number){
        out.print("\n"+ number+") ");
        if (ruleContext.getVariables().isEmpty()){
            out.println("yes\n");
            return false;
        }
        Set<Variable> seen = new HashSet<>();
        for (Variable var : ruleContext.getVariables()) {
            if(seen.contains(var)) continue;
            if(var.isFree()){
                Set<Variable> related = traverseRelated(var, seen);
                if(related.isEmpty())
                    out.print(var.getName()+ "=_");
                else {
                    out.print(var.getName());
                    for (Variable rel : related) {
                        if(var.getRuleContext()==rel.getRuleContext()) {
                            out.print("=" + rel);
                        }
                    }
                }
            }else{
                out.print(var.getName()+"="+var);
            }
            out.print("; ");
        }
        out.print("\n");
        return true;
    }

    private Set<Variable> traverseRelated(Variable start, Set<Variable> seen){
        Stack<Variable> traverse = new Stack<>();
        Set<Variable> related = new HashSet<>();
        traverse.push(start);
        while(!traverse.isEmpty()){
            Variable var = traverse.pop();
            if(!seen.add(var)) continue;
            if(var!=start && var.getRuleContext()==start.getRuleContext()) related.add(var);
            traverse.addAll(var.getRelated());
        }
        return related;
    }
}